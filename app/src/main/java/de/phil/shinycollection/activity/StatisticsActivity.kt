package de.phil.shinycollection.activity

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import de.phil.shinycollection.R
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.model.PokemonData
import de.phil.shinycollection.model.PokemonSortMethod
import de.phil.shinycollection.model.UpdateStatisticsData
import de.phil.shinycollection.viewmodel.StatisticsViewModel
import kotlinx.android.synthetic.main.activity_statistics.*


class StatisticsActivity : AppCompatActivity(), IPokemonListActivity {

    private lateinit var viewModel: StatisticsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTheme()
        setContentView(R.layout.activity_statistics)
        viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)

        setupStatistics()
        setupThemePokemonImage()
    }

    private fun setupThemePokemonImage() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when (prefs.getString(ShinyPokemonApplication.PREFERENCES_CURRENT_THEME, null)) {
            getString(R.string.theme_orange) -> statistics_imageView.setImageResource(R.drawable.leufeo)
            getString(R.string.theme_purple) -> statistics_imageView.setImageResource(R.drawable.wuffels)
            getString(R.string.theme_blue) -> statistics_imageView.setImageResource(R.drawable.scytherold)
            getString(R.string.theme_red) -> statistics_imageView.setImageResource(R.drawable.leufeo)
        }
    }

    private fun setupStatistics() {
        val statistics = viewModel.getStatistics()
        setStatisticsOnTextViews(statistics)
    }

    private fun setStatisticsOnTextViews(statistics: UpdateStatisticsData) {
        statistics_textView_number_shinys.text = (getString(R.string.num_shinys) + ": ${statistics.totalNumberOfShiny}")
        statistics_textView_number_shinys_eggs.text = (getString(R.string.num_shinys_eggs) + ": ${statistics.totalNumberOfEggShiny}")
        statistics_textView_number_shinys_sos.text = (getString(R.string.num_shinys_sos) + ": ${statistics.totalNumberOfSosShiny}")
        statistics_textView_average_shinys_sos.text = (getString(R.string.avg_shinys_sos) + ": ${statistics.averageSos}")
        statistics_textView_all_eggs.text = (getString(R.string.num_eggs) + ": ${statistics.totalEggs}")
        statistics_textView_average_eggs.text = (getString(R.string.avg_eggs) + ": ${statistics.averageEggs}")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun addRecyclerViewChangedListener(listener: MainActivity.OnListChangedListener) {

    }

    override fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData> {
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
