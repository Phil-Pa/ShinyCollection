package de.phil.solidsabissupershinysammlung.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.viewmodel.StatisticsViewModel
import kotlinx.android.synthetic.main.activity_statistics.*
import javax.inject.Inject


class StatisticsActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(StatisticsViewModel::class.java)

        val lineDataSet = LineDataSet(getData(), "Inducesmile")
//        lineDataSet.setGradientColor(android.R.color.white, android.R.color.holo_red_dark)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.fillColor = R.color.color_purple_sabi
        lineDataSet.valueTextColor = ContextCompat.getColor(this, R.color.color_purple_sabi)
        val xAxis: XAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
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

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
