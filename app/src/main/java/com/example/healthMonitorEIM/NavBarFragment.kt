package com.example.healthMonitorEIM

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.healthMonitorEIM.Model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class NavBarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.navigation_bar_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigationBar = requireView().findViewById<BottomNavigationView>(R.id.nav_view)

        bottomNavigationBar?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(context, DashboardActivity::class.java))
                }
                R.id.nav_alert -> {
                    println(false)

                    val items = arrayOf("Medic", "Persoana de Contact")
                    val builder = context?.let { AlertDialog.Builder(it) }

                    CoroutineScope(Dispatchers.Main).launch {
                        kotlin.runCatching {
                            withContext(Dispatchers.IO) {

                                MedicationApi.retrofitService.getUsers().enqueue(object : retrofit2.Callback<List<User>> {
                                    override fun onResponse(
                                        call: Call<List<User>>,
                                        response: Response<List<User>>
                                    ) {
                                        if (response.isSuccessful) {
                                            val connectedUser = response.body()!!.filter { user -> user.email == Firebase.auth.currentUser?.email.toString() }
                                            val contactPhone = connectedUser[0].contactPhoneNo
                                            val doctorPhone = connectedUser[0].doctorPhoneNo
                                            with(builder) {
                                                this?.setTitle("Contacte Urgente")
                                                this?.setItems(items) { _, which ->
                                                    if (items[which] == "Medic") {
                                                        val phoneCallUri = Uri.parse("tel:$doctorPhone")
                                                        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
                                                            it.data = phoneCallUri
                                                        }
                                                        startActivity(phoneCallIntent)
                                                    } else if (items[which] == "Persoana de Contact") {
                                                        val phoneCallUri = Uri.parse("tel:$contactPhone")
                                                        val phoneCallIntent = Intent(Intent.ACTION_DIAL).also {
                                                            it.data = phoneCallUri
                                                        }
                                                        startActivity(phoneCallIntent)
                                                    }
                                                }
                                                this?.show()
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<List<User>>, t: Throwable) {
                                        t.printStackTrace()
                                    }

                                })
                            }
                        }
                    }
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(context, MainActivity::class.java))
                }
            }
            true
        }
    }
}