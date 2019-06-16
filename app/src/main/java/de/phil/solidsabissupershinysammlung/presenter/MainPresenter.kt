package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import de.phil.solidsabissupershinysammlung.view.MainView

class MainPresenter(private val mainView: MainView) : MainViewPresenter {
    override fun getAverageEggsCount(): Float {
        return PokemonEngine.getAverageEggsCount().toFloat()
    }

    override fun getTotalEggsCount(): Int {
        return PokemonEngine.getTotalEggsCount()
    }

    override fun getTotalNumberOfShinys(): Int {
        return PokemonEngine.getTotalNumberOfShinys()
    }

    override fun deletePokemonFromDatabase(data: PokemonData) {
        PokemonEngine.deletePokemonFromDatabase(data)
    }

    override fun importData(data: String): Boolean {
        val dataList = data.split("\n")
        val regex = Regex("PokemonData\\(name=([\\w+\\-\\d]+), pokedexId=(\\d+), generation=(\\d), encounterNeeded=(\\d+), huntMethod=(\\w+), tabIndex=(\\d), internalId=(\\d+)\\)")

        PokemonEngine.deleteAllPokemonInDatabase()

        for (dataString in dataList) {

            // TODO find better solution

            if (dataString == "\n" || dataString.isEmpty() || dataString.isBlank())
                continue

            if (!regex.matches(dataString))
                return false

            val match = regex.matchEntire(dataString) ?: return false

            val name = match.groupValues[1]
            val pokedexId = match.groupValues[2].toInt()
            val generation = match.groupValues[3].toInt()
            val encounterNeeded = match.groupValues[4].toInt()
            val huntMethod = HuntMethod.valueOf(match.groupValues[5])
            val tabIndex = match.groupValues[6].toInt()
            val internalId = match.groupValues[7].toInt()

            PokemonEngine.addPokemon(PokemonData(name, pokedexId, generation, encounterNeeded, huntMethod, tabIndex, internalId))
        }

        return true
    }

    override fun exportData(): String {
        val pokemonList = mutableListOf<PokemonData>()
        for (i in 0 until App.NUM_TAB_VIEWS) {
            pokemonList.addAll(PokemonEngine.getAllPokemonInDatabaseFromTabIndex(i))
        }

        val sb = StringBuilder()

        for (pokemon in pokemonList) {
            sb.append(pokemon.toString()).append("\n")
        }

        return sb.toString()
    }
}