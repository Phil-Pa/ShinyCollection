package de.phil.shinycollection.database

import androidx.lifecycle.LiveData
import androidx.room.*
import de.phil.shinycollection.model.PokemonData

@Dao
interface PokemonDao {

    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0 AND pokemon_edition = :pokemonEditionOrdinal")
    fun getTotalNumberOfShinys(pokemonEditionOrdinal: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addPokemon(vararg data: PokemonData)

    @Update
    fun updatePokemon(data: PokemonData)

    @Delete
    fun deletePokemonFromDatabase(data: PokemonData)

    @Query("SELECT AVG(CAST(encounter_needed AS Float)), SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 0 AND tab_index = 0 AND pokemon_edition = :pokemonEditionOrdinal")
    fun getAverageEggsCount(pokemonEditionOrdinal: Int): Float

    @Query("SELECT SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 0 AND pokemon_edition = :pokemonEditionOrdinal")
    fun getTotalEggsCount(pokemonEditionOrdinal: Int): Int

    @Query("SELECT * FROM pokemondata")
    fun getAllPokemonData(): List<PokemonData>

    @Query("SELECT * FROM pokemondata WHERE tab_index = :tabIndex")
    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData>

    @Query("DELETE FROM pokemondata")
    fun deleteAllPokemonInDatabase()

    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0 AND hunt_method = 0 AND pokemon_edition = :pokemonEditionOrdinal")
    fun getTotalNumberOfEggShinys(pokemonEditionOrdinal: Int): Int

    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0 AND hunt_method = 1 AND pokemon_edition = :pokemonEditionOrdinal")
    fun getTotalNumberOfSosShinys(pokemonEditionOrdinal: Int): Int

    @Query("SELECT AVG(CAST(encounter_needed AS Float)), SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 1 AND tab_index = 0 AND pokemon_edition = :pokemonEditionOrdinal")
    fun getAverageSosCount(pokemonEditionOrdinal: Int): Float

    @Query("SELECT * FROM pokemondata WHERE tab_index = 0")
    fun getShinyListData(): LiveData<List<PokemonData>>

    @Query("SELECT MAX(internalId) from PokemonData")
    fun getMaxInternalId(): Int

    @Query("SELECT * FROM pokemondata WHERE :tabIndex = tab_index AND :pokemonEditionOrdinal = pokemon_edition ORDER BY RANDOM() LIMIT 1")
    fun getRandomPokemonData(tabIndex: Int, pokemonEditionOrdinal: Int): PokemonData?

    @Query("SELECT * FROM pokemondata WHERE :internalId = internalId LIMIT 1")
    fun getPokemonDataByInternalId(internalId: Int): PokemonData?
}