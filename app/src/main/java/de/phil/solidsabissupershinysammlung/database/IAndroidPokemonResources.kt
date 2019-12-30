package de.phil.solidsabissupershinysammlung.database

interface IAndroidPokemonResources {

    fun getPokemonNames(): List<String>
    fun getPokedexIdByName(name: String): Int
    fun getGenerationByName(name: String): Int
    fun getNameByPokedexId(pokedexId: Int): String

}