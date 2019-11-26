package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData

class DataExporter {

    var shouldCompressData = false

    fun export(repository: PokemonRepository): Pair<String, LitheString?>? {

        val pokemonList = mutableListOf<PokemonData>()

        for (i in 0 until App.NUM_TAB_VIEWS)
            pokemonList.addAll(repository.getAllPokemonDataFromTabIndex(i))

        if (pokemonList.isEmpty())
            return null

        val sb = StringBuilder()
        pokemonList.forEach { sb.append(it.toString()).append("\n") }

        val result = sb.toString()
        return if (shouldCompressData)
            Pair(result, compress(result))
        else
            Pair(result, null)
    }

    private fun compress(str: String): LitheString {
        return LitheString(str)
    }

}