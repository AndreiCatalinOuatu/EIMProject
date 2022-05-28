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
import com.example.healthMonitorEIM.Model.BloodSugar
import com.example.healthMonitorEIM.Model.Counters
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Response

class BloodSugarRegisterActivity : AppCompatActivity() {

    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        val phoneCallUri = Uri.parse("tel:" + "0743434867")

        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
            it.data = phoneCallUri
        }

        startActivity(phoneCallIntent)
    }

    private suspend fun printOnMainThread(input: String) {
        withContext(Dispatchers.Main) {
            val alertBuilder = AlertDialog.Builder(this@BloodSugarRegisterActivity)

            with(alertBuilder) {
                setTitle("Monitorizarea corecta a Glicemiei")
                setMessage(input)
                setPositiveButton("Am inteles", null)
                show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_bloodsugar)

        val guideBtn = findViewById<Button>(R.id.regBSGuide)
        val checkBSLevel = findViewById<Button>(R.id.checkBloodSugar)
        val registerBSLevel = findViewById<Button>(R.id.registerBloodSugar)
        val infoBS = findViewById<ImageButton>(R.id.bloodSugarInfo)
        val bloodSugar = findViewById<EditText>(R.id.bloodSugarValue)

        checkBSLevel.setOnClickListener {
            if (bloodSugar.length() == 0) {
                bloodSugar.error = "Completati cu valoarea glicemiei"
            } else {
                val bloodSugarVal = bloodSugar.text.toString().toInt()

                val builder = AlertDialog.Builder(this)

                with(builder) {
                    setTitle("ALERTA GLICEMIE ANORMALA")
                    if (bloodSugarVal < 74) {
                        setMessage("Glicemia este prea scazuta!")
                        setPositiveButton(
                            "Contacteaza Medic",
                            DialogInterface.OnClickListener(function = positiveButtonClick)
                        )
                        setNegativeButton("Am inteles", null)
                        show()
                    } else if (bloodSugarVal > 106) {
                        setMessage("Glicemia este prea ridicata!")
                        setPositiveButton(
                            "Contacteaza Medic",
                            DialogInterface.OnClickListener(function = positiveButtonClick)
                        )
                        setNegativeButton("Am inteles", null)
                        show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Glicemia dvs este in limitele normale!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        addBloodSugar(
                            BloodSugar(
                                bloodSugar.text.toString(),
                                System.currentTimeMillis(),
                                Firebase.auth.currentUser?.email.toString()
                            )
                        )
                    }
                }
            }
        }

        registerBSLevel.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    addBloodSugar(
                        BloodSugar(
                            bloodSugar.text.toString(),
                            System.currentTimeMillis(),
                            Firebase.auth.currentUser?.email.toString()
                        )
                    )
                }
            }
            Toast.makeText(
                applicationContext,
                "Glicemia a fost inregistrata cu succes!",
                Toast.LENGTH_SHORT
            ).show()
        }

        guideBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    val doc =
                        Jsoup.connect("https://viatacudiabet.ro/controlul-diabetului/despre-glicemie/cum-sa-iti-masori-corect-glicemia-50")
                            .get()
                    val el = doc.getElementById("article")
                    val links = el.select("p")
                    var msg = ""

                    for (link in links) {
                        msg += link.text().toString() + "\n"
                    }

                    printOnMainThread(msg)
                }
            }
        }

        infoBS.setOnClickListener {
            val infoBuilder = AlertDialog.Builder(this)

            with(infoBuilder) {
                setTitle("Glicemia")
                setMessage(
                    "Glicemia se referă la nivelul zahărului din sânge sau la concentrația " +
                            "glucozei din sânge. Hidrații de carbon, în principal zahărul și amidonul, " +
                            "au rolul de a furniza organismului energie. În prezența fermenților salivari, " +
                            "a enzimelor pancreatice și intestinale, acești hidrați se transformă în cea " +
                            "mai simplă formă de glucid, respectiv glucoză. Sub această formă, poate trece " +
                            "din intestinul subțire în sânge, pentru a putea fi folosită de către organism."
                )
                setPositiveButton("Am inteles", null)
                show()
            }
        }
    }

    private fun addBloodSugar(bloodSugar: BloodSugar) {
        var id: Long

        MedicationApi.retrofitService.getCounters().enqueue(object : retrofit2.Callback<Counters> {
            override fun onResponse(call: Call<Counters>, response: Response<Counters>) {
                if (response.isSuccessful) {
                    id = response.body()!!.counterBS
                    val updatedCounters = Counters(
                        response.body()!!.counterMeds,
                        response.body()!!.counterUsers,
                        response.body()!!.counterBP,
                        id + 1,
                        response.body()!!.counterHR,
                        response.body()!!.counterOS
                    )

                    MedicationApi.retrofitService.postBloodSugar(id, bloodSugar)
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
}