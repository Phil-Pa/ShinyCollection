package de.phil.solidsabissupershinysammlung.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.database.PokemonRepository
import de.phil.solidsabissupershinysammlung.model.PokemonData

class AddNewPokemonViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: PokemonRepository

    fun init(repository: PokemonRepository) {
        this.repository = repository
    }

    fun pokemonNameExists(name: String): Boolean {
        val pokemonNames = repository.getPokemonNames()
        for (pokemonName in pokemonNames)
            if (name == pokemonName)
                return true

        return false
    }

    fun validateInput(pokemonData: PokemonData): Pair<String?, PokemonData?> {

        val context = getApplication<Application>().applicationContext

        if (pokemonData.name.isEmpty() || pokemonData.name.isBlank())
            return Pair(context.resources.getString(R.string.error_empty_name), null)

        else if (!pokemonNameExists(pokemonData.name) && !pokemonData.name.endsWith("-alola"))
            return Pair("${pokemonData.name} " + context.resources.getString(R.string.error_is_not_a_pokemon), null)

        else if (pokemonData.encounterNeeded < 0)
            return Pair(context.resources.getString(R.string.error_encounter_lower_zero), null)

        val name = pokemonData.name
        val generation = repository.getGenerationByName(name)
        val pokedexId = repository.getPokedexIdByName(name)
        val internalId = repository.getMaxInternalId() + 1

        val validatedData = PokemonData(name, pokedexId, generation, pokemonData.encounterNeeded, pokemonData.huntMethod, pokemonData.tabIndex)
        validatedData.internalId = internalId

        return Pair(null, validatedData)
    }

    fun getPokedexIdByName(name: String): Int {
        return repository.getPokedexIdByName(name)
    }

    fun getGenerationByName(name: String): Int {
        return repository.getGenerationByName(name)
    }

}