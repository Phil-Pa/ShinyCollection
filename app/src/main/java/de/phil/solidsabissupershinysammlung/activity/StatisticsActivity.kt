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

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(StatisticsViewModel::class.java)

        val entries = viewModel.getDataEntries()
        setupChart(entries)
    }

    private fun setupChart(entries: List<Entry>) {

        val lineData = LineDataSet(entries, "Durchschnitt")
        lineData.setDrawFilled(true)
        lineData.setFillFormatter { dataSet, dataProvider -> lineChart.axisLeft.axisMinimum }
        lineData.mode = LineDataSet.Mode.CUBIC_BEZIER

        lineChart.setBackgroundColor(resources.getColor(android.R.color.white, theme))
        lineChart.setDrawGridBackground(false)
        lineChart.axisRight.isEnabled = false
        lineChart.data = LineData(lineData)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }
}
