package de.phil.solidsabissupershinysammlung.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
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
        viewModel = ViewModelProviders.of(this).get(StatisticsViewModel::class.java)
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

    override fun onListEntryLongClick(view: View) {

    }

    override fun onListEntryClick(pokemonData: PokemonData) {

    }
}
