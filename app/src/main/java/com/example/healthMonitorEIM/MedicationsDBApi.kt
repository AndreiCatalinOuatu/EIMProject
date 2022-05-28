package com.example.healthMonitorEIM

import com.example.healthMonitorEIM.Model.*
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val DB_URL = "https://healthmonitor-4c635-default-rtdb.europe-west1.firebasedatabase.app/"
private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi)).baseUrl(DB_URL).build()

interface MedicationsDBApi {

    @GET("Medications.json")
    fun getMedications(): Call<List<Medication>>

    @PUT("Medications/{id}.json")
    fun postMedication(@Path("id") id: Long, @Body medication: Medication): Call<Void>

    @GET("Users.json")
    fun getUsers(): Call<List<User>>

    @PUT("Users/{id}.json")
    fun postUser(@Path("id") id: Int, @Body user: User): Call<Void>

    @GET("Parameters/SystolicBP.json")
    fun getSystolicBP(): Call<List<SystolicBP>>

    @PUT("Parameters/SystolicBP/{id}.json")
    fun postSystolicBP(@Path("id") id: Int, @Body systolicBP: SystolicBP): Call<Void>

    @GET("Parameters/DiastolicBP.json")
    fun getDiastolicBP(): Call<List<DiastolicBP>>

    @PUT("Parameters/DiastolicBP/{id}.json")
    fun postDiastolicBP(@Path("id") id: Int, @Body diastolicBP: DiastolicBP): Call<Void>

    @GET("Parameters/HeartRate.json")
    fun getHeartRate(): Call<List<HeartRate>>

    @PUT("Parameters/HeartRate/{id}.json")
    fun postHearRate(@Path("id") id: Int, @Body heartRate: HeartRate): Call<Void>

    @GET("Parameters/BloodSugar.json")
    fun getBloodSugar(): Call<List<BloodSugar>>

    @PUT("Parameters/BloodSugar/{id}.json")
    fun postBloodSugar(@Path("id") id: Int, @Body bloodSugar: BloodSugar): Call<Void>

    @GET("Parameters/OxygenSaturation.json")
    fun getOxygenSaturation(): Call<List<OxygenSaturation>>

    @PUT("Parameters/OxygenSaturation/{id}.json")
    fun postOxygenSaturation(@Path(":id") id: Int, @Body oxygenSaturation: OxygenSaturation): Call<Void>

    @GET("Counters.json")
    fun getCounters(): Call<Counters>

    @PUT("Counters.json")
    fun postCounters(@Body counters: Counters): Call<Void>
}

object MedicationApi {
    val retrofitService: MedicationsDBApi by lazy {
        retrofit.create(MedicationsDBApi::class.java)
    }
}