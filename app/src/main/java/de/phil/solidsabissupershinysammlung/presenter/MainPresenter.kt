package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.MainView
import java.util.*
import kotlin.math.round

class MainPresenter(private val mainView: MainView) {
    fun sortData() {
        mainView.showDialog {sortMethod ->
            App.setSortMethod(sortMethod)
            for (i in 0 until App.NUM_TAB_VIEWS) {
                App.dataChangedListeners[i].notifySortPokemon(sortMethod)
            }
        }
    }

    fun setNavigationViewData() = mainView.updateShinyStatistics(App.pokemonEngine.getTotalNumberOfShinys(),
        App.pokemonEngine.getTotalNumberOfEggShiny(), App.pokemonEngine.getTotalNumberOfSosShinys(), App.pokemonEngine.getAverageSosCount().round(2), App.pokemonEngine.getTotalEggsCount(),
        App.pokemonEngine.getAverageEggsCount().toFloat().round(2))

    fun deletePokemonFromDatabase(data: PokemonData) = App.pokemonEngine.deletePokemonFromDatabase(data)

}

private fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return (round(this * multiplier) / multiplier)
}
