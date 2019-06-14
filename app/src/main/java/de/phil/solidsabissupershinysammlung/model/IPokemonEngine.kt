package de.phil.solidsabissupershinysammlung.model

import android.content.Context

interface IPokemonEngine {

    fun initialize(context: Context)

    fun getAllPokemonAlolaNames(): List<String>
    fun getTotalNumberOfShinys(): Int
    fun addPokemon(data: PokemonData, tabIndex: Int)
    fun getAverageEggsCount(): Double
    fun deletePokemonFromDatabase(data: PokemonData, tabIndex: Int)
    fun getTotalEggsCount(): Int
    fun getAllPokemonNames(): List<String>
    fun getAllPokemonInDatabaseFromTabIndex(tabIndex: Int): List<PokemonData>
    fun deletePokemonFromDatabaseWithName(pokemonName: String, tabIndex: Int)

    fun finish()

}