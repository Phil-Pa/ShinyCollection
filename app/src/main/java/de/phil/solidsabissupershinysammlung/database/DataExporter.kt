package de.phil.solidsabissupershinysammlung.database

class DataExporter {

    fun export(repository: PokemonRepository) : String? {
        val pokemonList = repository.getAllPokemonData()

        if (pokemonList.isEmpty())
            return null

        val sb = StringBuilder()
        pokemonList.forEach { sb.append(it.toString()).append("\n") }
        return sb.toString()
    }

}