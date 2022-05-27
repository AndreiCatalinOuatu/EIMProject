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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            startActivity(intent)
        }

        val emailTxt = findViewById<TextInputEditText>(R.id.emailET)
        val passwordTxt = findViewById<TextInputEditText>(R.id.passwordET)

        loginBtn.setOnClickListener {
            val email = emailTxt.text.toString()
            val password = passwordTxt.text.toString()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Signed in", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Wrong combination of username and passwpord", Toast.LENGTH_SHORT).show()
                    }
                }

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