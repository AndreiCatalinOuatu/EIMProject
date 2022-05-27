package com.example.healthMonitorEIM

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        auth = Firebase.auth

        val emailTxt = findViewById<TextInputEditText>(R.id.emailAuthET)
        val passwordTxt = findViewById<TextInputEditText>(R.id.passwordAuthET)
        val contactTxt = findViewById<TextInputEditText>(R.id.contactET)

        val registerBtn = findViewById<Button>(R.id.registerBtn)

        registerBtn.setOnClickListener {
            val email = emailTxt.text.toString()
            val password = passwordTxt.text.toString()
            val contactPhone = contactTxt.text.toString()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Account created!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AuthActivity, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Sign uo failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}