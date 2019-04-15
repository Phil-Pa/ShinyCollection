package de.phil.solidsabissupershinysammlung

import java.lang.StringBuilder

class PokemonData(val name: String, val pokedexId: Int, val generation: Int, val eggsNeeded: Int, val huntMethod: HuntMethod) {

    fun getDownloadUrl(): String {
        val baseString = "https://media.bisafans.de/d4c7a05/pokemon/gen7/sm/shiny/"
        val generationString = StringBuilder(pokedexId.toString())
        while (generationString.length < 3)
            generationString.insert(0, '0')

        return "$baseString$generationString.png"
    }

}