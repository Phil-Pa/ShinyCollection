package de.phil.solidsabissupershinysammlung.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository
import de.phil.solidsabissupershinysammlung.model.PokemonData
import javax.inject.Inject

// TODO: make something like an interface with class to store string messages to get rid of the application
class AddNewPokemonViewModel @Inject constructor(
    private val pokemonRepository: IPokemonRepository, application: Application
) : AndroidViewModel(application) {

    fun getPokemonNames(): List<String> {
        return pokemonRepository.getPokemonNames()
    }

    fun pokemonNameExists(name: String): Boolean {
        val pokemonNames = pokemonRepository.getPokemonNames()
        for (pokemonName in pokemonNames)
            if (name == pokemonName)
                return true

        return false
    }

    fun validateInput(pokemonData: PokemonData): Pair<String?, PokemonData?> {

        val context = getApplication<Application>().applicationContext

        if (pokemonData.name.isEmpty() || pokemonData.name.isBlank())
            return Pair(context.resources.getString(R.string.error_empty_name), null)
        else if (!pokemonNameExists(pokemonData.name) && !pokemonData.name.endsWith(App.ALOLA_EXTENSION) &&
            !pokemonData.name.endsWith(App.GALAR_EXTENSION)
        )
            return Pair(
                "${pokemonData.name} " + context.resources.getString(R.string.error_is_not_a_pokemon),
                null
            )
        else if (pokemonData.encounterNeeded < 0)
            return Pair(context.resources.getString(R.string.error_encounter_lower_zero), null)

        val name = pokemonData.name
        val generation = pokemonRepository.getGenerationByName(name)
        val pokedexId = pokemonRepository.getPokedexIdByName(name)
        val internalId = pokemonRepository.getMaxInternalId() + 1

        val validatedData = PokemonData(
            name,
            pokedexId,
            generation,
            pokemonData.encounterNeeded,
            pokemonData.huntMethod,
            pokemonData.tabIndex
        )
        validatedData.internalId = internalId

        return Pair(null, validatedData)
    }

    fun getPokedexIdByName(name: String): Int {
        return pokemonRepository.getPokedexIdByName(name)
    }

    fun getGenerationByName(name: String): Int {
        return pokemonRepository.getGenerationByName(name)
    }

}