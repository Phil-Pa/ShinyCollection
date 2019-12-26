package de.phil.solidsabissupershinysammlung.database

import androidx.lifecycle.LiveData
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

interface IPokemonRepository {

    fun insert(pokemonData: PokemonData)
    fun delete(pokemonData: PokemonData)
    fun deleteAll()
    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData>
    fun getTotalNumberOfShinys(pokemonEdition: PokemonEdition): Int
    fun getTotalNumberOfEggShinys(pokemonEdition: PokemonEdition): Int
    fun getTotalNumberOfSosShinys(pokemonEdition: PokemonEdition): Int
    fun getAverageSosEncounter(pokemonEdition: PokemonEdition): Float
    fun getTotalNumberOfHatchedEggs(pokemonEdition: PokemonEdition): Int
    fun getAverageEggsEncounter(pokemonEdition: PokemonEdition): Float
    fun getShinyListData(): LiveData<List<PokemonData>>
    fun setGuideShown()
    fun isGuideShown(): Boolean
    fun setSortMethod(sortMethod: PokemonSortMethod)
    fun getSortMethod(): PokemonSortMethod
    fun setShouldAutoSort(value: Boolean)
    fun shouldAutoSort(): Boolean
    fun setDataCompression(value: Boolean)
    fun shouldCompressData(): Boolean
    fun getPokemonNames(): List<String>
    fun getPokedexIdByName(name: String): Int
    fun getGenerationByName(name: String): Int
    fun getMaxInternalId(): Int
    fun getRandomPokemonData(tabIndex: Int): PokemonData?
    fun update(pokemonData: PokemonData)

    fun setPokemonEdition(pokemonEdition: PokemonEdition)
    fun getPokemonEdition(): PokemonEdition
    fun getAllPokemonData(): List<PokemonData>

}