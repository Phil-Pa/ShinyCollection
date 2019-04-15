package de.phil.solidsabissupershinysammlung

class PokemonData(val name: String, val pokedexId: Int, val generation: Int, val eggsNeeded: Int, val huntMethod: HuntMethod) {

    fun getDownloadUrl(): String = "https://media.bisafans.de/d4c7a05/pokemon/gen$generation/sm/shiny/$pokedexId.png"

}