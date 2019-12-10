package de.phil.solidsabissupershinysammlung.database

import androidx.lifecycle.LiveData
import androidx.room.*
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition

@Dao
interface PokemonDao {

    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0 AND pokemon_edition = :pokemonEdition")
    fun getTotalNumberOfShinys(pokemonEdition: PokemonEdition): Int

    @Insert
    fun addPokemon(vararg data: PokemonData)

    @Update
    fun updatePokemon(data: PokemonData)

    @Delete
    fun deletePokemonFromDatabase(data: PokemonData)

    @Query("SELECT AVG(CAST(encounter_needed AS Float)), SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 0 AND tab_index = 0 AND pokemon_edition = :pokemonEdition")
    fun getAverageEggsCount(pokemonEdition: PokemonEdition): Float

    @Query("SELECT SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 0 AND pokemon_edition = :pokemonEdition")
    fun getTotalEggsCount(pokemonEdition: PokemonEdition): Int

    @Query("SELECT * FROM pokemondata ORDER BY tab_index")
    fun getAllPokemonData(): LiveData<List<PokemonData>>

    @Query("SELECT * FROM pokemondata WHERE tab_index = :tabIndex")
    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData>

    @Query("DELETE FROM pokemondata")
    fun deleteAllPokemonInDatabase()

    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0 AND hunt_method = 0 AND pokemon_edition = :pokemonEdition")
    fun getTotalNumberOfEggShinys(pokemonEdition: PokemonEdition): Int

    @Query("SELECT COUNT(*) FROM pokemondata WHERE tab_index = 0 AND hunt_method = 1 AND pokemon_edition = :pokemonEdition")
    fun getTotalNumberOfSosShinys(pokemonEdition: PokemonEdition): Int

    @Query("SELECT AVG(CAST(encounter_needed AS Float)), SUM(encounter_needed) FROM pokemondata WHERE hunt_method = 1 AND tab_index = 0 AND pokemon_edition = :pokemonEdition")
    fun getAverageSosCount(pokemonEdition: PokemonEdition): Float

    @Query("SELECT * FROM pokemondata WHERE tab_index = 0")
    fun getShinyListData(): LiveData<List<PokemonData>>

    @Query("SELECT MAX(internalId) from PokemonData")
    fun getMaxInternalId(): Int

    @Query("SELECT * FROM pokemondata WHERE :tabIndex = tab_index ORDER BY RANDOM() LIMIT 1")
    fun getRandomPokemonData(tabIndex: Int): PokemonData?
}