package de.phil.solidsabissupershinysammlung.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.fragment.PokemonListFragment
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.viewmodel.StatisticsViewModel
import kotlinx.android.synthetic.main.activity_statistics.*


class StatisticsActivity : AppCompatActivity(), IPokemonListActivity {

    private lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTheme()
        setContentView(R.layout.activity_statistics)

        viewModel =
            ViewModelProviders.of(this).get(StatisticsViewModel::class.java)

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

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when (prefs.getString(App.PREFERENCES_CURRENT_THEME, null)) {
            "Sabi" -> statistics_imageView.setImageResource(R.drawable.leufeo)
            "Torben" -> statistics_imageView.setImageResource(R.drawable.wuffels)
            "Johannes" -> statistics_imageView.setImageResource(R.drawable.scytherold)
            "Phil" -> statistics_imageView.setImageResource(R.drawable.leufeo)
        }
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
    }

    override fun showSmallIcons(): Boolean {
        return true
    }

    override fun addRecyclerViewChangedListener(listener: MainActivity.OnListChangedListener) {

    }

    override fun getAllPokemonDataFromTabIndex(mTabIndex: Int): List<PokemonData> {
        return viewModel.getAllPokemon()
    }

    override fun getSortMethod(): PokemonSortMethod {
        return PokemonSortMethod.InternalId
    }

    override fun getContext(): Context {
        return this
    }

    override fun onListEntryLongClick(pokemonData: PokemonData) {

    }

    override fun onListEntryClick(pokemonData: PokemonData) {

    }
}
