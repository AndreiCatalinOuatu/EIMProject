package com.example.healthMonitorEIM

import android.os.Bundle
import android.os.DropBoxManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.security.KeyStore

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

            setDataToLineChart()
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