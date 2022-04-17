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

class OxygenSaturationRegisterActivity : AppCompatActivity() {

    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        val phoneCallUri = Uri.parse("tel:" + "0743434867")

        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
            it.data = phoneCallUri
        }

        startActivity(phoneCallIntent)
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

            with (infoBuilder) {
                setTitle("Saturatia de oxigen")
                setMessage("Saturatia de oxigen este unul dintre cei mai importanti indicatori pentru " +
                        "sanatatea ta. O saturatie scazuta poate duce la diferite complicatii si la " +
                        "necesitatea unei asistente medicale de urgenta sau a unei masti de oxigen. " +
                        "Sunt considerate normale pentru saturatia de oxigen valorile de peste 95%. " +
                        "Mai exact, cu cat acestea se apropie de 100%, cu atat mai bine este pentru " +
                        "organism si oxigenarea organelor acestuia. O valoare sub 95% a saturatiei " +
                        "de oxigen indica faptul ca starea de sanatate a pacientului trebuie " +
                        "monitorizata, iar cand valorile scad sub 90%, atunci pacientul are nevoie " +
                        "cu siguranta de asistenta medicala de urgenta sau ventilatie mecanica.")
                setPositiveButton("Am inteles", null)
                show()
            }
        }

        checkSpO2Btn.setOnClickListener {
            when {
                spO2.length() == 0 -> {
                    spO2.error = "Completati cu valoarea saturatiei de O2"
                }
                spO2.text.toString().toInt() < 90 -> {
                    val builder = AlertDialog.Builder(this)

                    with (builder) {
                        setTitle("ALERTA SATURATIE ANORMALA")
                        setMessage("SpO2 este sub limita normala!")
                        setPositiveButton("Contacteaza Medic", DialogInterface.OnClickListener(function = positiveButtonClick))
                        setNegativeButton("OK", null)
                        show()
                    }

                    // TODO: Add data to Database
                }
                else -> {
                    Toast.makeText(applicationContext, "Saturatia este normala", Toast.LENGTH_SHORT).show()
                    // TODO: Add data to Database
                }
            }
        }

        registerSpO2Btn.setOnClickListener {
            // TODO: Add data to Database
        }
    }
}