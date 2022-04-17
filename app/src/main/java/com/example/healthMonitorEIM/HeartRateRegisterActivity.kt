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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class HeartRateRegisterActivity : AppCompatActivity() {

    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        val phoneCallUri = Uri.parse("tel:" + "0743434867")

        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
            it.data = phoneCallUri
        }

        startActivity(phoneCallIntent)
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

            with (infoBuilder) {
                setTitle("Pulsul")
                setMessage("Pulsul este cunoscut ca masura a batailor inimii. In esenta pulsul " +
                        "reprezinta extinderea arterelor.  Această expansiune este cauzată de o " +
                        "creștere a tensiunii arteriale care împinge împotriva pereților elastici ai " +
                        "arterelor de fiecare dată când inima bate. Aceste extinderi cresc și se " +
                        "retrag odată cu inima, în timp ce pompează sângele, și apoi se odihnește pe " +
                        "măsură ce se reumple. " +
                        "Pulsările sunt resimțite în anumite puncte ale corpului, unde arterele mai " +
                        "mari se situează mai aproape de piele. " +
                        "In repaos pulsul ar trebui sa se situeze intre 60 si 100.")
                setPositiveButton("Am inteles", null)
                show()
            }
        }

        checkPulseBtn.setOnClickListener {
            if (pulseValue.length() == 0) {
                pulseValue.error = "Completati cu valoarea pulsului!"
            } else {
                val builder = AlertDialog.Builder(this)

                with (builder) {
                    setTitle("ALERTA PULS ANORMAL")
                    if (pulseValue.text.toString().toInt() < 60) {
                        setMessage("Pulsul este sub limita normala!")
                        setPositiveButton("Contacteaza Medic", DialogInterface.OnClickListener(function = positiveButtonClick))
                        setNegativeButton("OK", null)
                        show()
                    } else if (pulseValue.text.toString().toInt() > 100) {
                        setMessage("Pulsul este peste limita normala!")
                        setPositiveButton("Contacteaza Medic", DialogInterface.OnClickListener(function = positiveButtonClick))
                        setNegativeButton("OK", null)
                        show()
                    } else {
                        Toast.makeText(applicationContext, "Pulsul se afla in limite normale!", Toast.LENGTH_SHORT).show()
                    }
                }

                // TODO: Add data in DataBase
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

                    while (!node.nextSibling().toString().contains("Frecvența cardiacă", ignoreCase = true)) {
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
            // TODO: Add data in Database
        }
    }
}