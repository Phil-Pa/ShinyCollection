package de.phil.solidsabissupershinysammlung.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.phil.solidsabissupershinysammlung.model.PokemonData

@Dao
interface PokemonDao {

    // TODO: add @Query
    fun getAllPokemonAlolaNames(): List<String>

    @Query("SELECT COUNT(*) FROM pokemondata WHERE hunt_method = ")
    fun getTotalNumberOfShinys(): Int

    @Insert
    fun addPokemon(data: PokemonData)

    @Delete
    fun deletePokemon(data: PokemonData)

    fun getAverageEggsCount(): Double
    fun deletePokemonFromDatabase(data: PokemonData)
    fun getTotalEggsCount(): Int
    fun getAllPokemonNames(): List<String>
    fun getAllPokemonInDatabaseFromTabIndex(tabIndex: Int): List<PokemonData>
    fun deletePokemonFromDatabaseWithName(pokemonName: String, tabIndex: Int)

    @Query("DELETE FROM pokemondata")
    fun deleteAllPokemonInDatabase()

    @Query("SELECT MAX(*) FROM pokemondata")
    fun getMaxInternalId(): Int

    @Query("SELECT COUNT(*) FROM pokemondata")
    fun getNumberOfDataSets(): Int
    fun pokemonNameExists(name: String): Boolean

    fun finish()
    fun getTotalNumberOfEggShiny(): Int
    fun getTotalNumberOfSosShinys(): Int

    fun getNamesArray(index: Int): Array<String>
    fun getPokedexIdsArray(index: Int): Array<Int>
    fun getAverageSosCount(): Float
    fun getPokedexIdByName(text: String): Int
}