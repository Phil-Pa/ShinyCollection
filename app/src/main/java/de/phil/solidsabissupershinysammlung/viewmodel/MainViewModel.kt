package de.phil.solidsabissupershinysammlung.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.database.DataExporter
import de.phil.solidsabissupershinysammlung.database.DataImporter
import de.phil.solidsabissupershinysammlung.database.PokemonRepository
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.model.UpdateStatisticsData
import de.phil.solidsabissupershinysammlung.utils.round
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: PokemonRepository

    fun init(repository: PokemonRepository) {
        this.repository = repository
    }

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
        val pokemonList = repository.getAllPokemonData().value

        if (pokemonList == null || pokemonList.isEmpty())
            return null

        return pokemonList[Random.nextInt(pokemonList.size)]
    }

    fun getAllPokemonData(): LiveData<List<PokemonData>> {
        return repository.getAllPokemonData()
    }

    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData> {
        return repository.getAllPokemonDataFromTabIndex(tabIndex)
    }

    fun deletePokemon(pokemonToDelete: PokemonData) {
        repository.delete(pokemonToDelete)
    }

    fun getStatisticsData(): UpdateStatisticsData {
        val totalShinys = repository.getTotalNumberOfShinys()
        val totalEggShinys = repository.getTotalNumberOfEggShinys()
        val totalSosShinys = repository.getTotalNumberOfSosShinys()
        val averageSos = repository.getAverageSosEncounter().round(2)
        val totalEggs = repository.getTotalNumberOfHatchedEggs()
        val averageEggs = repository.getAverageEggsEncounter().round(2)

        return UpdateStatisticsData(totalShinys, totalEggShinys, totalSosShinys, averageSos, totalEggs, averageEggs)
    }

    fun getShinyListData(): LiveData<List<PokemonData>> {
        return repository.getShinyListData()
    }

    //region preferences

    fun setGuideShown() {
        repository.setGuideShown()
    }

    fun isGuideShown(): Boolean {
        return repository.isGuideShown()
    }

    fun setSortMethod(pokemonSortMethod: PokemonSortMethod) {
        repository.setSortMethod(pokemonSortMethod)
    }

    fun getSortMethod(): PokemonSortMethod {
        return repository.getSortMethod()
    }

    fun shouldAutoSort(): Boolean {
        return repository.shouldAutoSort()
    }

    //endregion

}