package de.phil.shinycollection.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.phil.shinycollection.R
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.database.PokemonDao
import de.phil.shinycollection.database.PokemonDatabase
import de.phil.shinycollection.model.PokemonData

class AddNewPokemonViewModel(application: Application) : AndroidViewModel(application) {

    private val pokemonDao: PokemonDao =
        PokemonDatabase.instance(application.applicationContext).pokemonDao()

    fun pokemonNameExists(name: String) = name in getPokemonNames()

    private fun getPokemonNames() = PokemonDatabase.androidPokemonResources(getApplication()).getPokemonNames()

    fun getPokemonNamesFormsInclusive() = PokemonDatabase.androidPokemonResources(getApplication()).getPokemonNamesFormsInclusive()

    private fun validateInput(pokemonData: PokemonData): Pair<String?, PokemonData?> {

        val context = getApplication<Application>().applicationContext

        if (pokemonNameEmpty(pokemonData))
            return Pair(context.resources.getString(R.string.error_empty_name), null)
        else if (!pokemonNameExists(pokemonData.name) && pokemonNameHasNoExtension(pokemonData.name))
            return Pair("${pokemonData.name} " + context.resources.getString(R.string.error_is_not_a_pokemon), null)
        else if (pokemonData.encounterNeeded < 0)
            return Pair(context.resources.getString(R.string.error_encounter_lower_zero), null)

        val name = pokemonData.name
        val generation = PokemonDatabase.androidPokemonResources(getApplication()).getGenerationByName(name)
        val pokedexId = PokemonDatabase.androidPokemonResources(getApplication()).getPokedexIdByName(name)
        val internalId = pokemonDao.getMaxInternalId() + 1

        val validatedData = PokemonData(
            name,
            pokedexId,
            generation,
            pokemonData.encounterNeeded,
            pokemonData.huntMethod,
            pokemonData.pokemonEdition,
            pokemonData.tabIndex
        )
        validatedData.internalId = internalId

        return Pair(null, validatedData)
    }

    private fun pokemonNameHasNoExtension(name: String): Boolean {
        return name.endsWith(
            ShinyPokemonApplication.ALOLA_EXTENSION) &&
                name.endsWith(ShinyPokemonApplication.GALAR_EXTENSION)
    }

    private fun pokemonNameEmpty(pokemonData: PokemonData) = pokemonData.name.isEmpty() || pokemonData.name.isBlank()

    fun getPokedexIdByName(name: String) = PokemonDatabase.androidPokemonResources(getApplication()).getPokedexIdByName(name)

    fun getGenerationByName(name: String) = PokemonDatabase.androidPokemonResources(getApplication()).getGenerationByName(name)

    fun addPokemonToDatabase(pokemonData: PokemonData): Pair<Boolean, String> {

        val (message, data) = validateInput(pokemonData)

        return if (message == null && data != null) {
            pokemonData.internalId = pokemonDao.getMaxInternalId() + 1
            pokemonData.generation = PokemonDatabase.androidPokemonResources(getApplication()).getGenerationByName(pokemonData.name)
            pokemonData.pokedexId = PokemonDatabase.androidPokemonResources(getApplication()).getPokedexIdByName(pokemonData.name)
            pokemonDao.addPokemon(pokemonData)
            Pair(true, "${pokemonData.name} " + getApplication<Application>().applicationContext.resources.getString(R.string.message_has_been_added))
        } else {
            Pair(false, message!!)
        }
    }

}