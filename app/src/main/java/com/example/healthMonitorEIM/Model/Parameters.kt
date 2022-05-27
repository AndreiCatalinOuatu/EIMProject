package com.example.healthMonitorEIM.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class SystolicBP(
    @SerializedName("value")
    @Expose
    var value: String,

    @SerializedName("timestamp")
    @Expose
    var timestamp: Timestamp,

    @SerializedName("user")
    @Expose
    var user: String
)

data class DiastolicBP(
    @SerializedName("value")
    @Expose
    var value: String,

    @SerializedName("timestamp")
    @Expose
    var timestamp: Timestamp,

    @SerializedName("user")
    @Expose
    var user: String
)

data class BloodSugar(
    @SerializedName("value")
    @Expose
    var value: String,

    @SerializedName("timestamp")
    @Expose
    var timestamp: Timestamp,

    @SerializedName("user")
    @Expose
    var user: String
)

data class HeartRate(
    @SerializedName("value")
    @Expose
    var value: String,

    @SerializedName("timestamp")
    @Expose
    var timestamp: Timestamp,

    @SerializedName("user")
    @Expose
    var user: String
)

data class OxygenSaturation(
    @SerializedName("value")
    @Expose
    var value: String,

    @SerializedName("timestamp")
    @Expose
    var timestamp: Timestamp,

    @SerializedName("user")
    @Expose
    var user: String
)