package de.phil.solidsabissupershinysammlung.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.phil.solidsabissupershinysammlung.database.DataExporter
import de.phil.solidsabissupershinysammlung.database.DataImporter
import de.phil.solidsabissupershinysammlung.database.PokemonRepository
import de.phil.solidsabissupershinysammlung.model.PokemonData
import kotlin.random.Random

class MainViewModel(application: Application, private val repository: PokemonRepository) : AndroidViewModel(application) {

    fun addPokemon(pokemonData: PokemonData) {
        repository.insert(pokemonData)
    }

    fun import(data: String?): Boolean {

        val importer = DataImporter()
        return importer.import(repository, data)

        // TODO: observe in activity
//        App.performAutoSort()
//        setNavigationViewData()
    }

    fun export(): String? {
        val exporter = DataExporter()
        return exporter.export(repository)
    }

    fun getRandomPokemon(): PokemonData? {
        val pokemonList = repository.getAllPokemonData()

        if (pokemonList.isEmpty())
            return null

        return pokemonList[Random.nextInt(pokemonList.size)]
    }

}