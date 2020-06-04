package de.phil.solidsabissupershinysammlung.database

class DataExporter {

    var shouldCompressData = false

    fun export(pokemonDao: PokemonDao): String? {

        val pokemonList = pokemonDao.getAllPokemonData()

        if (pokemonList.isEmpty())
            return null

        val sb = StringBuilder()
        pokemonList.forEach { sb.append(if (shouldCompressData) it.toShortString() else it.toString()).append("\n") }

        return sb.toString()
    }
}