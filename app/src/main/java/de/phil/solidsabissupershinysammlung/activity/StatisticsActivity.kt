package de.phil.solidsabissupershinysammlung.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
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

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(StatisticsViewModel::class.java)

        val entries = viewModel.getDataEntries()
        setupChart(entries)
        initStatistics()
    }

    private fun initStatistics() {

        val statistics = viewModel.getStatistics()

        statistics_textView_number_shinys.text = (getString(R.string.num_shinys) + ": ${statistics.totalNumberOfShiny}")
        statistics_textView_number_shinys_eggs.text = (getString(R.string.num_shinys_eggs) + ": ${statistics.totalNumberOfEggShiny}")
        statistics_textView_number_shinys_sos.text = (getString(R.string.num_shinys_sos) + ": ${statistics.totalNumberOfSosShiny}")
        statistics_textView_average_shinys_sos.text = (getString(R.string.avg_shinys_sos) + ": ${statistics.averageSos}")
        statistics_textView_all_eggs.text = (getString(R.string.num_eggs) + ": ${statistics.totalEggs}")
        statistics_textView_average_eggs.text = (getString(R.string.avg_eggs) + ": ${statistics.averageEggs}")
    }

    private fun setupChart(entries: List<Entry>) {

        val lineData = LineDataSet(entries, "Durchschnitt gebrüteter Eier")
        lineData.setDrawCircleHole(false)
        lineData.setDrawCircles(false)
        lineData.setDrawFilled(true)
        lineData.fillDrawable = resources.getDrawable(R.drawable.navigation_drawer_image, theme)
        lineData.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineData.setDrawValues(false)
        lineData.disableDashedLine()
        lineData.disableDashedHighlightLine()

        lineChart.description.isEnabled = false
        lineChart.setBackgroundColor(resources.getColor(android.R.color.white, theme))
        lineChart.setDrawGridBackground(false)
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.data = LineData(lineData)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)


        lineChart.animateX(1500)
        lineChart.invalidate()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
