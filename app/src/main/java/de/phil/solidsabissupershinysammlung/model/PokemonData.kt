package de.phil.solidsabissupershinysammlung.model

import de.phil.solidsabissupershinysammlung.core.App

data class PokemonData(val name: String, val pokedexId: Int, val generation: Int, val encounterNeeded: Int, val huntMethod: HuntMethod, val tabIndex: Int,
                       val internalId: Int) {

    private fun isAlola() = App.pokemonEngine.getAllPokemonAlolaNames().contains(name)

    fun getDownloadUrl(): String {
        val baseString = "https://media.bisafans.de/d4c7a05/pokemon/gen7/sm/shiny/"
        val generationString = StringBuilder(pokedexId.toString())
        while (generationString.length < 3)
            generationString.insert(0, '0')

        if (isAlola()) {
            generationString.append("-alola")
        }

        return "$baseString$generationString.png"
    }

}