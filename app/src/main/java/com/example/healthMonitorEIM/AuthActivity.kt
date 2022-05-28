package com.example.healthMonitorEIM

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthMonitorEIM.Model.Counters
import com.example.healthMonitorEIM.Model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

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
                        Toast.makeText(applicationContext, "Account created!", Toast.LENGTH_SHORT)
                            .show()
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.IO) {
                                addUser(User(email, contactPhone))
                            }
                        }
                        val intent = Intent(this@AuthActivity, DashboardActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Sign up failed!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun addUser(user: User) {
        var id: Long

        MedicationApi.retrofitService.getCounters().enqueue(object : retrofit2.Callback<Counters> {
            override fun onResponse(call: Call<Counters>, response: Response<Counters>) {
                if (response.isSuccessful) {
                    id = response.body()!!.counterUsers
                    val updatedCounters = Counters(
                        response.body()!!.counterMeds,
                        id + 1,
                        response.body()!!.counterBP,
                        response.body()!!.counterBS,
                        response.body()!!.counterHR,
                        response.body()!!.counterOS
                    )
                    MedicationApi.retrofitService.postUser(id, user)
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