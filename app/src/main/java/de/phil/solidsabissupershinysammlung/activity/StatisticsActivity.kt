package de.phil.solidsabissupershinysammlung.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import de.phil.solidsabissupershinysammlung.R
import kotlinx.android.synthetic.main.activity_statistics.*


class StatisticsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        val lineDataSet = LineDataSet(getData(), "Inducesmile")
//        lineDataSet.setGradientColor(android.R.color.white, android.R.color.holo_red_dark)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.fillColor = R.color.color_purple_sabi
        lineDataSet.valueTextColor = ContextCompat.getColor(this, R.color.color_purple_sabi)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val months = arrayOf("Jan", "Feb", "Mar", "Apr")
        val formatter: ValueFormatter =
            object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return months[value.toInt()]
                }
            }
        xAxis.granularity = 1f
//        xAxis.valueFormatter = formatter
        val yAxisRight: YAxis = lineChart.axisRight
        yAxisRight.isEnabled = false
        val yAxisLeft: YAxis = lineChart.axisLeft
        yAxisLeft.granularity = 1f
        val data = LineData(lineDataSet)
        lineChart.data = data
        lineChart.animateX(2500)
        lineChart.invalidate()
    }

    private fun getData(): List<Entry> {
        val entries = mutableListOf<Entry>()
        for (i in 0..100) {
            val temp = i.toFloat()
            entries.add(Entry(temp, kotlin.random.Random.nextFloat() * 10f))
        }
        return entries
    }
}
