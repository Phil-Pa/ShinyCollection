package de.phil.solidsabissupershinysammlung.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
        if (pokemonData.name.isEmpty() || pokemonData.name.isBlank())
            return Pair("Der Name darf nicht leer sein!", null)

        else if (!pokemonNameExists(pokemonData.name))
            return Pair("${pokemonData.name} ist kein Pokemon!", null)

        else if (pokemonData.encounterNeeded < 0)
            return Pair("Die Encounter müssen größer gleich 0 sein.", null)

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

}