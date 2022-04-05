package com.example.myapplication

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val helloMsg = findViewById<TextView>(R.id.hello)
        val user = "User"
        helloMsg.text = "Buna, $user!"

        val monitorParamBtn = findViewById<Button>(R.id.monitorParameters)
        val alertBtn = findViewById<Button>(R.id.alert)
        val addNewMedicationBtn = findViewById<Button>(R.id.addNewMedication)
        val viewStatsBtn = findViewById<Button>(R.id.viewStats)
        val viewMedicationsBtn = findViewById<Button>(R.id.viewMedicationDetails)

        alertBtn.setOnClickListener {
            val items = arrayOf("Medic", "Persoana de Contact")
            val builder = AlertDialog.Builder(this)

            with (builder) {
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

            with (builder) {
                setTitle("Parametri")
                setItems(items) { _, which ->
                    chosenParameter = items[which]
                    if (chosenParameter == "Tensiune Arteriala") {
                        startActivity(Intent(applicationContext, BloodPressureActivity::class.java))
                    } else if (chosenParameter == "Puls") {
                        startActivity(Intent(applicationContext, HeartRateRegisterActivity::class.java))
                    } else if (chosenParameter == "Glicemie") {
                        startActivity(Intent(applicationContext, BloodSugarRegisterActivity::class.java))
                    } else if (chosenParameter == "SpO2") {
                        startActivity(Intent(applicationContext, OxygenSaturationRegisterActivity::class.java))
                    } else if (chosenParameter == "Indice de stres") {
                        startActivity(Intent(applicationContext, StressRegisterActivity::class.java))
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

            builder.setTitle("Adauga Tratament Nou")
            builder.setPositiveButton("Adauga Tratament") { _, _ ->
                Toast.makeText(
                    applicationContext,
                    medicationNameTxt.text.toString() + pillsPerDayTxt.text.toString() + durationTxt.text.toString() + doctorPhoneNo.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()



                // TODO: Add these values to database
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
}