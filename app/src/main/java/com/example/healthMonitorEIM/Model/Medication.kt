package com.example.healthMonitorEIM.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Medication(
    @SerializedName("Days")
    @Expose
    var days: String,

    @SerializedName("DoctorPhone")
    @Expose
    var doctorPhone: String,

    @SerializedName("Name")
    @Expose
    var name: String,

    @SerializedName("PillsPerDay")
    @Expose
    var pillsPerDay: String,

    @SerializedName("User")
    @Expose
    var user: String
)