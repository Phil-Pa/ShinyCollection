package de.phil.shinycollection.database

interface IAndroidPokemonResources {

    fun getPokemonNames(): List<String>
    fun getPokedexIdByName(name: String): Int
    fun getGenerationByName(name: String): Int
    fun getNameByPokedexId(pokedexId: Int): String
    fun getPokemonNamesFormsInclusive(): List<String>

}