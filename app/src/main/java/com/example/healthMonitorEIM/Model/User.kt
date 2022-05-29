package com.example.healthMonitorEIM.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("email")
    @Expose
    var email: String,

    @SerializedName("ContactPersonPhone")
    @Expose
    var contactPhoneNo: String,

    @SerializedName("DoctorPhone")
    @Expose
    var doctorPhoneNo: String
)