package com.example.healthMonitorEIM.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Counters(
    @SerializedName("counterMeds")
    @Expose
    var counterMeds: Long,

    @SerializedName("counterUsers")
    @Expose
    var counterUsers: Long,

    @SerializedName("counterBP")
    @Expose
    var counterBP: Long,

    @SerializedName("counterBS")
    @Expose
    var counterBS: Long,

    @SerializedName("counterHR")
    @Expose
    var counterHR: Long,

    @SerializedName("counterOS")
    @Expose
    var counterOS: Long
)