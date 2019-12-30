package de.phil.solidsabissupershinysammlung.database

import de.phil.android.lib.encoding.Base64StringCompression
import de.phil.android.lib.encoding.shannon.ShannonAlgorithm
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData

class DataExporter {

    var shouldCompressData = false

    fun export(repository: IPokemonRepository): String? {

        val pokemonList = mutableListOf<PokemonData>()

        for (i in 0 until App.NUM_TAB_VIEWS)
            pokemonList.addAll(repository.getAllPokemonDataFromTabIndex(i))

        if (pokemonList.isEmpty())
            return null

        val sb = StringBuilder()
        pokemonList.forEach { sb.append(if (shouldCompressData) it.toShortString() else it.toString()).append("\n") }

        return sb.toString()
    }
}