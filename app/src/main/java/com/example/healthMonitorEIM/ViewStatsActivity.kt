package com.example.healthMonitorEIM

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.healthMonitorEIM.Model.BloodPressure
import com.example.healthMonitorEIM.Model.BloodSugar
import com.example.healthMonitorEIM.Model.HeartRate
import com.example.healthMonitorEIM.Model.OxygenSaturation
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Response

class ViewStatsActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_stats)

        val statsSpinner = findViewById<Spinner>(R.id.statsSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.parameters_array,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            statsSpinner.adapter = arrayAdapter
        }

        val periodSpinner = findViewById<Spinner>(R.id.periodSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.periods_array,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            periodSpinner.adapter = arrayAdapter
        }

        lineChart = findViewById(R.id.lineChartParameters)

        val viewStatsBtn = findViewById<Button>(R.id.viewStatsBtn)

        viewStatsBtn.setOnClickListener {
            initLineChart()

            val selectedPeriod = periodSpinner.selectedItem.toString()
            val selectedStat = statsSpinner.selectedItem.toString()
            var userHeartRates: List<HeartRate>
            var userBloodSugar: List<BloodSugar>
            var userOxygenSaturation: List<OxygenSaturation>
            var userBloodPressure: List<BloodPressure>

            when (selectedStat) {
                "Puls" -> {
                    MedicationApi.retrofitService.getHeartRate()
                        .enqueue(object : retrofit2.Callback<List<HeartRate>> {
                            override fun onResponse(
                                call: Call<List<HeartRate>>,
                                response: Response<List<HeartRate>>
                            ) {
                                when (selectedPeriod) {
                                    "Ultimele 3 zile" -> {
                                        userHeartRates = response.body()!!
                                            .filter { heartRate -> (heartRate.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - heartRate.timestamp < 1000 * 3600 * 24 * 3) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userHeartRates.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userHeartRates[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima saptamana" -> {
                                        userHeartRates = response.body()!!
                                            .filter { heartRate -> (heartRate.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - heartRate.timestamp < 1000 * 3600 * 24 * 7) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userHeartRates.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userHeartRates[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima luna" -> {
                                        userHeartRates = response.body()!!
                                            .filter { heartRate -> (heartRate.user == Firebase.auth.currentUser?.email.toString()) && (((System.currentTimeMillis() - heartRate.timestamp) / 1000) < 3600 * 24 * 30) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userHeartRates.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userHeartRates[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<List<HeartRate>>, t: Throwable) {
                                t.printStackTrace()
                            }

                        })
                }
                "Glicemie" -> {
                    MedicationApi.retrofitService.getBloodSugar()
                        .enqueue(object : retrofit2.Callback<List<BloodSugar>> {
                            override fun onResponse(
                                call: Call<List<BloodSugar>>,
                                response: Response<List<BloodSugar>>
                            ) {
                                when (selectedPeriod) {
                                    "Ultimele 3 zile" -> {
                                        userBloodSugar = response.body()!!
                                            .filter { bloodSugar -> (bloodSugar.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - bloodSugar.timestamp < 1000 * 3600 * 24 * 3) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodSugar.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodSugar[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima saptamana" -> {
                                        userBloodSugar = response.body()!!
                                            .filter { bloodSugar -> (bloodSugar.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - bloodSugar.timestamp < 1000 * 3600 * 24 * 7) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodSugar.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodSugar[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima luna" -> {
                                        userBloodSugar = response.body()!!
                                            .filter { bloodSugar -> (bloodSugar.user == Firebase.auth.currentUser?.email.toString()) && (((System.currentTimeMillis() - bloodSugar.timestamp) / 1000) < 3600 * 24 * 30) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodSugar.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodSugar[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<List<BloodSugar>>, t: Throwable) {
                                t.printStackTrace()
                            }

                        })
                }
                "Saturatia de oxigen" -> {
                    MedicationApi.retrofitService.getOxygenSaturation()
                        .enqueue(object : retrofit2.Callback<List<OxygenSaturation>> {
                            override fun onResponse(
                                call: Call<List<OxygenSaturation>>,
                                response: Response<List<OxygenSaturation>>
                            ) {
                                when (selectedPeriod) {
                                    "Ultimele 3 zile" -> {
                                        userOxygenSaturation = response.body()!!
                                            .filter { oxygenSaturation -> (oxygenSaturation.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - oxygenSaturation.timestamp < 1000 * 3600 * 24 * 3) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userOxygenSaturation.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userOxygenSaturation[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima saptamana" -> {
                                        userOxygenSaturation = response.body()!!
                                            .filter { oxygenSaturation -> (oxygenSaturation.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - oxygenSaturation.timestamp < 1000 * 3600 * 24 * 7) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userOxygenSaturation.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userOxygenSaturation[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima luna" -> {
                                        userOxygenSaturation = response.body()!!
                                            .filter { oxygenSaturation -> (oxygenSaturation.user == Firebase.auth.currentUser?.email.toString()) && (((System.currentTimeMillis() - oxygenSaturation.timestamp) / 1000) < 3600 * 24 * 30) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userOxygenSaturation.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userOxygenSaturation[i].value.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                }
                            }

                            override fun onFailure(
                                call: Call<List<OxygenSaturation>>,
                                t: Throwable
                            ) {
                                t.printStackTrace()
                            }

                        })
                }
                "Tensiune Arteriala Sistolica" -> {
                    MedicationApi.retrofitService.getBloodPressure()
                        .enqueue(object : retrofit2.Callback<List<BloodPressure>> {
                            override fun onResponse(
                                call: Call<List<BloodPressure>>,
                                response: Response<List<BloodPressure>>
                            ) {
                                when (selectedPeriod) {
                                    "Ultimele 3 zile" -> {
                                        userBloodPressure = response.body()!!
                                            .filter { bloodPressure -> (bloodPressure.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - bloodPressure.timestamp < 1000 * 3600 * 24 * 3) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodPressure.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodPressure[i].systolicBPValue.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima saptamana" -> {
                                        userBloodPressure = response.body()!!
                                            .filter { bloodPressure -> (bloodPressure.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - bloodPressure.timestamp < 1000 * 3600 * 24 * 7) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodPressure.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodPressure[i].systolicBPValue.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima luna" -> {
                                        userBloodPressure = response.body()!!
                                            .filter { bloodPressure -> (bloodPressure.user == Firebase.auth.currentUser?.email.toString()) && (((System.currentTimeMillis() - bloodPressure.timestamp) / 1000) < 3600 * 24 * 30) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodPressure.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodPressure[i].systolicBPValue.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<List<BloodPressure>>, t: Throwable) {
                                t.printStackTrace()
                            }

                        })
                }
                "Tensiune Arteriala Diastolica" -> {
                    MedicationApi.retrofitService.getBloodPressure()
                        .enqueue(object : retrofit2.Callback<List<BloodPressure>> {
                            override fun onResponse(
                                call: Call<List<BloodPressure>>,
                                response: Response<List<BloodPressure>>
                            ) {
                                when (selectedPeriod) {
                                    "Ultimele 3 zile" -> {
                                        userBloodPressure = response.body()!!
                                            .filter { bloodPressure -> (bloodPressure.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - bloodPressure.timestamp < 1000 * 3600 * 24 * 3) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodPressure.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodPressure[i].diastolicBPValue.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima saptamana" -> {
                                        userBloodPressure = response.body()!!
                                            .filter { bloodPressure -> (bloodPressure.user == Firebase.auth.currentUser?.email.toString()) && (System.currentTimeMillis() - bloodPressure.timestamp < 1000 * 3600 * 24 * 7) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodPressure.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodPressure[i].diastolicBPValue.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                    "Ultima luna" -> {
                                        userBloodPressure = response.body()!!
                                            .filter { bloodPressure -> (bloodPressure.user == Firebase.auth.currentUser?.email.toString()) && (((System.currentTimeMillis() - bloodPressure.timestamp) / 1000) < 3600 * 24 * 30) }
                                        val entries: ArrayList<Entry> = ArrayList()

                                        for (i in userBloodPressure.indices) {
                                            entries.add(
                                                Entry(
                                                    i.toFloat(),
                                                    userBloodPressure[i].diastolicBPValue.toFloat()
                                                )
                                            )
                                        }

                                        val lineDataSet = LineDataSet(entries, "")
                                        val data = LineData(lineDataSet)

                                        lineChart.data = data

                                        lineChart.invalidate()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<List<BloodPressure>>, t: Throwable) {
                                t.printStackTrace()
                            }

                        })
                }
            }

        }
    }

    private fun initLineChart() {
        lineChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        lineChart.axisRight.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.description.isEnabled = false

        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
    }

    private fun setDataToLineChart() {
        val entries: ArrayList<Entry> = ArrayList()

        for (i in 1..30) {
            entries.add(Entry(i.toFloat(), (120 - 2 * i).toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "")
        val data = LineData(lineDataSet)

        lineChart.data = data

        lineChart.invalidate()
    }
}