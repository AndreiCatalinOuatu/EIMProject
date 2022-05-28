package com.example.healthMonitorEIM

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthMonitorEIM.Model.Medication

class RecyclerAdapter(private val medicationList: ArrayList<Medication>) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var medicationName: TextView = view.findViewById(R.id.cardMedName) as TextView
        var medicationDuration: TextView = view.findViewById(R.id.cardMedDuration) as TextView
        var medicationPillsPerDay: TextView = view.findViewById(R.id.cardMedPillsPerDay) as TextView
        var medicationDoctorPhone: TextView = view.findViewById(R.id.cardDoctorPhone) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.medication_cardview, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.medicationName.text = "Denumire Medicament: " + medicationList[position].name
            holder.medicationDuration.text = "Zile de Tratament: " + medicationList[position].days
        holder.medicationPillsPerDay.text = "Numar de comprimate pe zi: " + medicationList[position].pillsPerDay
        holder.medicationDoctorPhone.text = "Telefon medic: " + medicationList[position].doctorPhone
    }

    override fun getItemCount(): Int {
        return medicationList.size
    }
}