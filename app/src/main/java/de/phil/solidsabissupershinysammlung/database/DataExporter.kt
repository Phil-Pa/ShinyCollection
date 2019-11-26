package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData

class DataExporter {

    fun export(repository: PokemonRepository) : String? {

        val pokemonList = mutableListOf<PokemonData>()

        for (i in 0 until App.NUM_TAB_VIEWS)
            pokemonList.addAll(repository.getAllPokemonDataFromTabIndex(i))

        if (pokemonList.isEmpty())
            return null

        val sb = StringBuilder()
        pokemonList.forEach { sb.append(it.toString()).append("\n") }

        return sb.toString()



    }

}