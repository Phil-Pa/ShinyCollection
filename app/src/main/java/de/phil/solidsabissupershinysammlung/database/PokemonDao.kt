package de.phil.solidsabissupershinysammlung.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import de.phil.solidsabissupershinysammlung.model.PokemonData

// TODO: add @Query

@Dao
interface PokemonDao {

    // TODO: extract the names stuff to an extra part of the repository
//    fun getAllPokemonAlolaNames(): List<String>
//    fun getAllPokemonNames(): List<String>
//    fun pokemonNameExists(name: String): Boolean
//    fun getNamesArray(index: Int): Array<String>
//    fun getPokedexIdsArray(index: Int): Array<Int>
//    fun getPokedexIdByName(text: String): Int
//    fun finish()

    // TODO: check if equals is done with one =
    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0")
    fun getTotalNumberOfShinys(): Int

    @Insert
    fun addPokemon(vararg data: PokemonData)

    @Delete
    fun deletePokemonFromDatabase(data: PokemonData)

    @Query("SELECT AVG(Cast(encounter_needed as Float)), SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 0")
    fun getAverageEggsCount(): Float

    @Query("SELECT SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 0")
    fun getTotalEggsCount(): Int

    @Query("SELECT * FROM pokemondata ORDER BY tab_index")
    fun getAllPokemonData(): LiveData<List<PokemonData>>

    @Query("SELECT * FROM pokemondata WHERE tab_index = :tabIndex")
    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData>

    @Query("DELETE FROM pokemondata")
    fun deleteAllPokemonInDatabase()

    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0 AND hunt_method = 0")
    fun getTotalNumberOfEggShinys(): Int

    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0 AND hunt_method = 1")
    fun getTotalNumberOfSosShinys(): Int

    @Query("SELECT AVG(Cast(encounter_needed as Float)), SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 1")
    fun getAverageSosCount(): Float

    @Query("SELECT * FROM pokemondata WHERE tab_index = 0")
    fun getShinyListData(): LiveData<List<PokemonData>>
}