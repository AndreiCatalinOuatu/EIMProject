package com.example.healthMonitorEIM

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class BloodPressureActivity : AppCompatActivity() {

    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        val phoneCallUri = Uri.parse("tel:" + "0743434867")

        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
            it.data = phoneCallUri
        }

        startActivity(phoneCallIntent)
    }

    private suspend fun printOnMainThread(input: String) {
        withContext(Main) {
            val alertBuilder = AlertDialog.Builder(this@BloodPressureActivity)

            with (alertBuilder) {
                setTitle("Monitorizarea corecta a TA")
                setMessage(input)
                setPositiveButton("Am inteles", null)
                show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_bloodpressure)

        val systolicBP = findViewById<EditText>(R.id.tensiune_sistolica)
        val diastolicBP = findViewById<EditText>(R.id.tensiune_diastolica)

        val checkBP = findViewById<Button>(R.id.verifica)
        val infoBP = findViewById<ImageButton>(R.id.infoTA)
        val registerBP = findViewById<Button>(R.id.adaugaTA)
        val guideBtn = findViewById<Button>(R.id.verificaGhid)

        infoBP.setOnClickListener {
            val infoBuilder = AlertDialog.Builder(this)

            with (infoBuilder) {
                setTitle("Tensiunea Arteriala")
                setMessage("Tensiunea arteriala este inregistrata sub forma a doua numere.\nPrimul " +
                        "numar sau tensiunea arteriala sistolica masoara presiunea in artere cand " +
                        "inima se contracta si ar trebui sa fie mai mica decat 140 si mai mare decat 90 " +
                        "in repaos.\n" +
                        "Al doilea numar sau tensiunea arteriala diastolica masoara presiunea in " +
                        "artere cand inima se relaxeaza si ar trebui sa fie mai mica decat 90 si " +
                        "mai mare decat 60.")
                setPositiveButton("Am inteles", null)
                show()
            }
        }

        checkBP.setOnClickListener {
            if (systolicBP.length() == 0 && diastolicBP.length() == 0) {
                systolicBP.error = "Completati cu valoarea TA sistolice"
                diastolicBP.error = "Completati cu valoarea TA diastolice"
            } else if (diastolicBP.length() == 0) {
                diastolicBP.error = "Completati cu valoarea TA diastolice!"
            } else if (systolicBP.length() == 0) {
                systolicBP.error ="Completati cu valoarea TA sitolice!"
            } else {
                val systolicBPValue = systolicBP.text.toString().toInt()
                val diastolicBPValue = diastolicBP.text.toString().toInt()

                val builder = AlertDialog.Builder(this)

                with (builder) {
                    setTitle("ALERTA TENSIUNE ARTERIALA ANORMALA")
                    if (systolicBPValue < 90 || diastolicBPValue < 60) {
                        setMessage("Tensiunea arteriala este prea scazuta!")
                        setPositiveButton("Contacteaza Medic", DialogInterface.OnClickListener(function = positiveButtonClick))
                        setNegativeButton("Am inteles", null)
                        show()
                    } else if (systolicBPValue > 120 || diastolicBPValue > 90) {
                        setMessage("Tensiunea arteriala este prea ridicata!")
                        setPositiveButton("Contacteaza Medic", DialogInterface.OnClickListener(function = positiveButtonClick))
                        setNegativeButton("Am inteles", null)
                        show()
                    } else {
                        Toast.makeText(applicationContext, "Tensiunea dvs este in limitele normale!", Toast.LENGTH_SHORT).show()
                    }
                }

                // TODO: Add data in Database
            }
        }

        registerBP.setOnClickListener {
            if (systolicBP.length() > 0 && diastolicBP.length() > 0) {
                Toast.makeText(applicationContext, "Tensiunea a fost adaugata cu succes!", Toast.LENGTH_SHORT).show()
            } else {
                if (systolicBP.length() == 0 && diastolicBP.length() == 0) {
                    systolicBP.error = "Completati cu valoarea TA sistolice!"
                    diastolicBP.error = "Completati cu valoarea TA diastolice!"
                } else if (diastolicBP.length() == 0) {
                    diastolicBP.error = "Completati cu valoarea TA diastolice!"
                } else {
                    systolicBP.error = "Completati cu valoarea TA sistolice!"
                }
            }
        }

        guideBtn.setOnClickListener {
            CoroutineScope(IO).launch {
                kotlin.runCatching {
                    val doc = Jsoup.connect("https://www.reginamaria.ro/articole-medicale/invata-sa-ti-masori-corect-tensiunea-arteriala").get()
                    /*val el = doc.getElementById("article")
                    val links = el.select("p")
                    var msg = ""

                    for (link in links) {
                        msg += link.text().toString() + "\n"
                    }

                   printOnMainThread(msg)*/
                }
            }
        }
    }

}