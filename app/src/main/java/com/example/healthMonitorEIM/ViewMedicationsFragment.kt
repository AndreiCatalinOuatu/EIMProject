package com.example.healthMonitorEIM

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthMonitorEIM.Model.Medication
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class ViewMedicationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_medications_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            kotlin.runCatching {
                withContext(Dispatchers.Default) {
                    val viewRec = requireView().findViewById<RecyclerView>(R.id.recyclerView)
                    MedicationApi.retrofitService.getMedications()
                        .enqueue(object : retrofit2.Callback<List<Medication>> {
                            override fun onResponse(
                                call: Call<List<Medication>>,
                                response: Response<List<Medication>>
                            ) {
                                if (response.isSuccessful) {
                                    val userMedications = response.body()!!
                                        .filter { medication -> medication.user == Firebase.auth.currentUser?.email.toString() }
                                    val adapter = RecyclerAdapter(userMedications as ArrayList<Medication>)
                                    viewRec.layoutManager = LinearLayoutManager(activity?.applicationContext, RecyclerView.VERTICAL, false)
                                    viewRec.adapter = adapter
                                }
                            }
                            override fun onFailure(call: Call<List<Medication>>, t: Throwable) {
                                t.printStackTrace()
                            }

                        })
                }
            }
        }
    }

}