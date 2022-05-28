package com.example.healthMonitorEIM

import android.app.*
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Build.*
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.healthMonitorEIM.Model.Counters
import com.example.healthMonitorEIM.Model.Medication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.*
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private lateinit var auth: FirebaseAuth

    private fun createNotificationChannel() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val channelName = "notifyHealthMonitorChannel"
            val description = "Channel for Medication Reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, channelName, importance)
            channel.description = description

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val helloMsg = findViewById<TextView>(R.id.hello)
        auth = Firebase.auth
        val user = auth.currentUser?.email.toString()
        helloMsg.text = "Buna, $user!"

        val monitorParamBtn = findViewById<Button>(R.id.monitorParameters)
        val alertBtn = findViewById<Button>(R.id.alert)
        val addNewMedicationBtn = findViewById<Button>(R.id.addNewMedication)
        val viewStatsBtn = findViewById<Button>(R.id.viewStats)
        val viewMedicationsBtn = findViewById<Button>(R.id.viewMedicationDetails)

        alertBtn.setOnClickListener {
            val items = arrayOf("Medic", "Persoana de Contact")
            val builder = AlertDialog.Builder(this)

            with(builder) {
                setTitle("Contacte Urgente")
                setItems(items) { _, which ->
                    if (items[which] == "Medic") {
                        val phoneCallUri = Uri.parse("tel:" + "0743434867")
                        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
                            it.data = phoneCallUri
                        }
                        startActivity(phoneCallIntent)
                    } else if (items[which] == "Persoana de Contact") {
                        val phoneCallUri = Uri.parse("tel:" + "0722222222")
                        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
                            it.data = phoneCallUri
                        }
                        startActivity(phoneCallIntent)
                    }
                }
                show()
            }
        }

        monitorParamBtn.setOnClickListener {
            val items = arrayOf("Tensiune Arteriala", "Puls", "Glicemie", "SpO2", "Indice de stres")
            val builder = AlertDialog.Builder(this)
            var chosenParameter = ""

            with(builder) {
                setTitle("Parametri")
                setItems(items) { _, which ->
                    chosenParameter = items[which]
                    if (chosenParameter == "Tensiune Arteriala") {
                        startActivity(Intent(applicationContext, BloodPressureActivity::class.java))
                    } else if (chosenParameter == "Puls") {
                        startActivity(
                            Intent(
                                applicationContext,
                                HeartRateRegisterActivity::class.java
                            )
                        )
                    } else if (chosenParameter == "Glicemie") {
                        startActivity(
                            Intent(
                                applicationContext,
                                BloodSugarRegisterActivity::class.java
                            )
                        )
                    } else if (chosenParameter == "SpO2") {
                        startActivity(
                            Intent(
                                applicationContext,
                                OxygenSaturationRegisterActivity::class.java
                            )
                        )
                    } else if (chosenParameter == "Indice de stres") {
                        startActivity(
                            Intent(
                                applicationContext,
                                StressRegisterActivity::class.java
                            )
                        )
                    }
                }
                show()
            }
        }

        addNewMedicationBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val builderView = inflater.inflate(R.layout.add_medication_dialog_layout, null)
            builder.setView(builderView)

            val medicationNameTxt = builderView.findViewById<EditText>(R.id.medicationName)
            val pillsPerDayTxt = builderView.findViewById<EditText>(R.id.pillsPerDay)
            val durationTxt = builderView.findViewById<EditText>(R.id.duration)
            val doctorPhoneNo = builderView.findViewById<EditText>(R.id.doctorPhoneNo)

            createNotificationChannel()

            builder.setTitle("Adauga Tratament Nou")
            builder.setPositiveButton("Adauga Tratament") { _, _ ->
                scheduleNotification()
                // TODO: Add these values to database
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        addMedication(
                            Medication(
                                durationTxt.text.toString(),
                                doctorPhoneNo.text.toString(),
                                medicationNameTxt.text.toString(),
                                pillsPerDayTxt.text.toString(),
                                user
                            )
                        )
                    }
                }
            }
            val b = builder.create()
            b.show()
        }

        viewStatsBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ViewStatsActivity::class.java))
        }

        viewMedicationsBtn.setOnClickListener {
            startActivity(Intent(applicationContext, ViewMedicationsActivity::class.java))
        }
    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        intent.putExtra(titleExtra, "Notificare medicamentatie")
        intent.putExtra(messageExtra, "Aveti de luat 3 Nurofen astazi")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 11)
            set(Calendar.MINUTE, 14)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            1000 * 60 * 60 * 24,
            pendingIntent
        )
    }

    private fun addMedication(medication: Medication) {
        var id: Long

        MedicationApi.retrofitService.getCounters().enqueue(object : retrofit2.Callback<Counters> {
            override fun onResponse(call: Call<Counters>, response: Response<Counters>) {
                if (response.isSuccessful) {
                    id = response.body()!!.counterMeds
                    val updatedCounters = Counters(
                        id + 1,
                        response.body()!!.counterUsers,
                        response.body()!!.counterSBP,
                        response.body()!!.counterDBP,
                        response.body()!!.counterBS,
                        response.body()!!.counterHR,
                        response.body()!!.counterOS
                    )
                    MedicationApi.retrofitService.postMedication(id, medication).enqueue(object :
                        retrofit2.Callback<Void> {
                        override fun onResponse(
                            call: Call<Void>,
                            response: Response<Void>
                        ) {
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