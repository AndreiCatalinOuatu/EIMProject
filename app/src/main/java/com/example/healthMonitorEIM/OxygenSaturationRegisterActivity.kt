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
import com.example.healthMonitorEIM.Model.OxygenSaturation
import com.example.healthMonitorEIM.Model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class OxygenSaturationRegisterActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_oxygensaturation)

        val spO2infoBtn = findViewById<ImageButton>(R.id.oxygenSaturationInfo)
        val spO2 = findViewById<EditText>(R.id.oxygenSaturationValue)
        val checkSpO2Btn = findViewById<Button>(R.id.checkOxygenSaturation)
        val registerSpO2Btn = findViewById<Button>(R.id.registerOxygenSaturation)

        spO2infoBtn.setOnClickListener {
            val infoBuilder = AlertDialog.Builder(this)

            with(infoBuilder) {
                setTitle("Saturatia de oxigen")
                setMessage(
                    "Saturatia de oxigen este unul dintre cei mai importanti indicatori pentru " +
                            "sanatatea ta. O saturatie scazuta poate duce la diferite complicatii si la " +
                            "necesitatea unei asistente medicale de urgenta sau a unei masti de oxigen. " +
                            "Sunt considerate normale pentru saturatia de oxigen valorile de peste 95%. " +
                            "Mai exact, cu cat acestea se apropie de 100%, cu atat mai bine este pentru " +
                            "organism si oxigenarea organelor acestuia. O valoare sub 95% a saturatiei " +
                            "de oxigen indica faptul ca starea de sanatate a pacientului trebuie " +
                            "monitorizata, iar cand valorile scad sub 90%, atunci pacientul are nevoie " +
                            "cu siguranta de asistenta medicala de urgenta sau ventilatie mecanica."
                )
                setPositiveButton("Am inteles", null)
                show()
            }
        }

        checkSpO2Btn.setOnClickListener {
            val level = getSPO2level(spO2.text.toString())
            val msg = level.second
            val abnormalSaturation = level.first
            when {
                spO2.length() == 0 -> {
                    spO2.error = "Completati cu valoarea saturatiei de O2"
                }
                abnormalSaturation -> {
                    val builder = AlertDialog.Builder(this)

                    with(builder) {
                        setTitle("ALERTA SATURATIE ANORMALA")
                        setMessage(msg)
                        setPositiveButton(
                            "Contacteaza Medic",
                            DialogInterface.OnClickListener(function = positiveButtonClick)
                        )
                        setNegativeButton("OK", null)
                        show()
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            addOxygenSaturation(
                                OxygenSaturation(
                                    spO2.text.toString(),
                                    System.currentTimeMillis(),
                                    Firebase.auth.currentUser?.email.toString()
                                )
                            )
                        }
                    }
                }
                else -> {
                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
                        .show()
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO) {
                            addOxygenSaturation(
                                OxygenSaturation(
                                    spO2.text.toString(),
                                    System.currentTimeMillis(),
                                    Firebase.auth.currentUser?.email.toString()
                                )
                            )
                        }
                    }
                }
            }
        }

        registerSpO2Btn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    addOxygenSaturation(
                        OxygenSaturation(
                            spO2.text.toString(),
                            System.currentTimeMillis(),
                            Firebase.auth.currentUser?.email.toString()
                        )
                    )
                }
            }
            Toast.makeText(
                applicationContext,
                "Saturatia de O2 a fost inregistrata cu succes!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun addOxygenSaturation(oxygenSaturation: OxygenSaturation) {
        var id: Long

        MedicationApi.retrofitService.getCounters().enqueue(object : retrofit2.Callback<Counters> {
            override fun onResponse(call: Call<Counters>, response: Response<Counters>) {
                if (response.isSuccessful) {
                    id = response.body()!!.counterOS
                    val updatedCounters = Counters(
                        response.body()!!.counterMeds,
                        response.body()!!.counterUsers,
                        response.body()!!.counterBP,
                        response.body()!!.counterBS,
                        response.body()!!.counterHR,
                        id + 1
                    )
                    MedicationApi.retrofitService.postOxygenSaturation(id, oxygenSaturation)
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

    fun getSPO2level(oxygenSaturationValue: String) : Pair<Boolean, String> {
        return when {
            oxygenSaturationValue.toInt() < 90 -> {
                Pair(true, "Saturatia este sub limita normala!")
            } else -> {
                Pair(false, "Saturatia este in limite normale!")
            }
        }
    }
}