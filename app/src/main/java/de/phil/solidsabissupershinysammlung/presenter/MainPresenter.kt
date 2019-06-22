package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import de.phil.solidsabissupershinysammlung.view.MainView
import java.util.*
import kotlin.math.round

class MainPresenter(private val mainView: MainView) : MainViewPresenter {
    override fun sortData() {
        mainView.showDialog {sortMethod ->
            App.setSortMethod(sortMethod)
            for (i in 0 until App.NUM_TAB_VIEWS) {
                App.dataChangedListeners[i].notifySortPokemon(sortMethod)
            }
        }
    }

    override fun startAddNewPokemonActivity() {
        val tabIndex = mainView.getCurrentTabIndex()
        if (tabIndex < 0 || tabIndex > App.NUM_TAB_VIEWS) {
            throw IllegalStateException()
        }

        mainView.startAddNewPokemonActivity(tabIndex)
    }

    override fun showRandomPokemon() {
        val pokemon = PokemonEngine.getAllPokemonInDatabaseFromTabIndex(mainView.getCurrentTabIndex())

        if (pokemon.isEmpty()) {
            mainView.showMessage("There is no Pokemon in the list")
            return
        }

        val random = Random()
        mainView.showMessage(pokemon[random.nextInt(pokemon.size)].name)
    }

    override fun setNavigationViewData() = mainView.updateShinyStatistics(PokemonEngine.getTotalNumberOfShinys(), PokemonEngine.getTotalEggsCount(), PokemonEngine.getAverageEggsCount().toFloat().round(2))

    override fun deletePokemonFromDatabase(data: PokemonData) = PokemonEngine.deletePokemonFromDatabase(data)

    override fun importData() {

        val data = mainView.getClipboardStringData()

        if (data == null) {
            mainView.showMessage("Could not import data")
            return
        }

        val dataList = data.split("\n")
        val regex = Regex("PokemonData\\(name=([\\w+\\-\\d:]+), pokedexId=(\\d+), generation=(\\d), encounterNeeded=(\\d+), huntMethod=(\\w+), tabIndex=(\\d), internalId=(\\d+)\\)")

        PokemonEngine.deleteAllPokemonInDatabase()

        for (dataString in dataList) {

            // TODO find better solution

            if (dataString == "\n" || dataString.isEmpty() || dataString.isBlank())
                continue

            if (!regex.matches(dataString)) {
                mainView.showMessage("Could not import data")
                return
            }

            val match: MatchResult? = regex.matchEntire(dataString)

            if (match == null) {
                mainView.showMessage("Could not import data")
                return
            }

            val name = match.groupValues[1]
            val pokedexId = match.groupValues[2].toInt()
            val generation = match.groupValues[3].toInt()
            val encounterNeeded = match.groupValues[4].toInt()
            val huntMethod = HuntMethod.valueOf(match.groupValues[5])
            val tabIndex = match.groupValues[6].toInt()
            val internalId = match.groupValues[7].toInt()

            PokemonEngine.addPokemon(PokemonData(name, pokedexId, generation, encounterNeeded, huntMethod, tabIndex, internalId))
        }
        App.performAutoSort()
        setNavigationViewData()
    }

    override fun exportData() {

        val pokemonList = mutableListOf<PokemonData>()
        for (i in 0 until App.NUM_TAB_VIEWS) {
            pokemonList.addAll(PokemonEngine.getAllPokemonInDatabaseFromTabIndex(i))
        }

        val sb = StringBuilder()

        for (pokemon in pokemonList) {
            sb.append(pokemon.toString()).append("\n")
        }

        mainView.copyToClipboard(sb.toString())
    }
}

private fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return (round(this * multiplier) / multiplier)
}
