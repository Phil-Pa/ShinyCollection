package de.phil.solidsabissupershinysammlung.database

class DataExporter {

    fun export(repository: PokemonRepository) : String? {
        val pokemonList = repository.getAllPokemonData().value

        if (pokemonList == null || pokemonList.isEmpty())
            return null

        val sb = StringBuilder()
        pokemonList.forEach { sb.append(it.toString()).append("\n") }
        return sb.toString()
    }

}