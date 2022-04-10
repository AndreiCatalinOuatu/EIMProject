package com.example.healthMonitorEIM

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginBtn = findViewById<Button>(R.id.loginBtn)

        loginBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            startActivity(intent)
        }

        val noAccountTxt = findViewById<TextView>(R.id.authText)

        val txt = "Nu ai cont? Creeaza unul"
        val ss = SpannableString(txt)

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(applicationContext, AuthActivity::class.java)
                startActivity(intent)
            }
        }

        ss.setSpan(clickableSpan1, 12, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        noAccountTxt.text = ss
        noAccountTxt.movementMethod = LinkMovementMethod.getInstance()
    }
}