package com.example.healthMonitorEIM

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthMonitorEIM.Model.Counters
import com.example.healthMonitorEIM.Model.HeartRate
import com.example.healthMonitorEIM.Model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class HeartRateRegisterActivity : AppCompatActivity() {

    private val positiveButtonClick = { _: DialogInterface, _: Int ->

        CoroutineScope(Dispatchers.Main).launch {
            kotlin.runCatching {
                withContext(Dispatchers.IO) {
                    MedicationApi.retrofitService.getUsers().enqueue(object : Callback<List<User>> {
                        override fun onResponse(
                            call: Call<List<User>>,
                            response: Response<List<User>>
                        ) {
                            if (response.isSuccessful) {
                                val users = response.body()!!
                                val connectedUser = users.filter { user -> user.email == Firebase.auth.currentUser?.email.toString() }[0]
                                val phoneCallUri = Uri.parse("tel: " + connectedUser.doctorPhoneNo)
                                val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
                                    it.data = phoneCallUri
                                }
                                startActivity(phoneCallIntent)
                            }
                        }

                        override fun onFailure(call: Call<List<User>>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })
                }
            }
        }
        Unit
    }

    private suspend fun printOnMainThread(input: String) {
        withContext(Dispatchers.Main) {
            val alertBuilder = AlertDialog.Builder(this@HeartRateRegisterActivity)

            with(alertBuilder) {
                setTitle("Monitorizarea corecta a Pulsului")
                setMessage(input)
                setPositiveButton("Am inteles", null)
                show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_heartrate)

        val pulseValue = findViewById<EditText>(R.id.pulseValue)
        val checkPulseBtn = findViewById<Button>(R.id.checkPulse)
        val registerPulseBtn = findViewById<Button>(R.id.registerPulse)
        val infoPulseBtn = findViewById<ImageButton>(R.id.pulseInfo)
        val checkPulseGuideBtn = findViewById<Button>(R.id.checkHrGuide)

        infoPulseBtn.setOnClickListener {
            val infoBuilder = AlertDialog.Builder(this)

            with(infoBuilder) {
                setTitle("Pulsul")
                setMessage(
                    "Pulsul este cunoscut ca masura a batailor inimii. In esenta pulsul " +
                            "reprezinta extinderea arterelor.  Această expansiune este cauzată de o " +
                            "creștere a tensiunii arteriale care împinge împotriva pereților elastici ai " +
                            "arterelor de fiecare dată când inima bate. Aceste extinderi cresc și se " +
                            "retrag odată cu inima, în timp ce pompează sângele, și apoi se odihnește pe " +
                            "măsură ce se reumple. " +
                            "Pulsările sunt resimțite în anumite puncte ale corpului, unde arterele mai " +
                            "mari se situează mai aproape de piele. " +
                            "In repaos pulsul ar trebui sa se situeze intre 60 si 100."
                )
                setPositiveButton("Am inteles", null)
                show()
            }
        }

        checkPulseBtn.setOnClickListener {
            if (pulseValue.length() == 0) {
                pulseValue.error = "Completati cu valoarea pulsului!"
            } else {
                val builder = AlertDialog.Builder(this)
                val level = heartRateLevel(pulseValue.text.toString())
                val msg = level.second
                val abnormalPulse = level.first
                with(builder) {
                    if (abnormalPulse) {
                        setTitle("ALERTA PULS ANORMAL")
                        setMessage(msg)
                        setPositiveButton(
                            "Contacteaza Medic",
                            DialogInterface.OnClickListener(function = positiveButtonClick)
                        )
                        setNegativeButton("OK", null)
                        show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            msg,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        addHeartRate(
                            HeartRate(
                                pulseValue.text.toString(),
                                System.currentTimeMillis(),
                                Firebase.auth.currentUser?.email.toString()
                            )
                        )
                    }
                }
            }
        }

        checkPulseGuideBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    val doc =
                        Jsoup.connect("https://www.romedic.ro/masurarea-pulsului-0P34728")
                            .get()
                    val el = doc.getElementById("localizarea-pulsului")
                    var node = el.nextSibling()
                    var msg = node.toString()

                    while (!node.nextSibling().toString()
                            .contains("Frecvența cardiacă", ignoreCase = true)
                    ) {
                        node = node.nextSibling()
                        msg += node.toString()
                    }

                    msg = msg.replace("<br>", "").replace("<ul>", "")
                        .replace("<li>", "").replace("<ul>", "")
                        .replace("<b>", "").replace("</b>", "")
                        .replace("</li>", "").replace("</ul>", "")
                        .replace("&nbsp;", "")

                    printOnMainThread(msg)
                }
            }
        }

        registerPulseBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    addHeartRate(
                        HeartRate(
                            pulseValue.text.toString(),
                            System.currentTimeMillis(),
                            Firebase.auth.currentUser?.email.toString()
                        )
                    )
                }
            }
            Toast.makeText(
                applicationContext,
                "Pulsul a fost inregistrat cu succes!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun addHeartRate(heartRate: HeartRate) {
        var id: Long

        MedicationApi.retrofitService.getCounters().enqueue(object : retrofit2.Callback<Counters> {
            override fun onResponse(call: Call<Counters>, response: Response<Counters>) {
                if (response.isSuccessful) {
                    id = response.body()!!.counterHR
                    val updatedCounters = Counters(
                        response.body()!!.counterMeds,
                        response.body()!!.counterUsers,
                        response.body()!!.counterBP,
                        response.body()!!.counterBS,
                        id + 1,
                        response.body()!!.counterOS
                    )

                    MedicationApi.retrofitService.postHearRate(id, heartRate)
                        .enqueue(object : retrofit2.Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    MedicationApi.retrofitService.postCounters(updatedCounters)
                                        .enqueue(object : retrofit2.Callback<Void> {
                                            override fun onResponse(
                                                call: Call<Void>,
                                                response: Response<Void>
                                            ) {
                                            }

                                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                                t.printStackTrace()
                                            }

                                        })
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                t.printStackTrace()
                            }

                        })
                }
            }

            override fun onFailure(call: Call<Counters>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    fun heartRateLevel(pulseValue: String) : Pair<Boolean, String>{
        return when {
            pulseValue.toInt() < 60 -> {
                Pair(true, "Pulsul este sub limita normala!")
            }
            pulseValue.toInt() > 100 -> {
                Pair(true, "Pulsul este peste limita normala!")
            }
            else -> {
                Pair(false, "Pulsul se afla in limite normale!")
            }
        }
    }
}