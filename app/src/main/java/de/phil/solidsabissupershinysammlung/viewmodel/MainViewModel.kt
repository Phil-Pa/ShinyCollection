package de.phil.solidsabissupershinysammlung.viewmodel

import android.app.Application
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

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var repository: PokemonRepository
    private val exporter = DataExporter()
    private val importer = DataImporter()

    fun init(repository: PokemonRepository) {
        this.repository = repository
    }

    fun addPokemon(pokemonData: PokemonData) {
        repository.insert(pokemonData)
    }

    fun updatePokemon(pokemonData: PokemonData) {
        repository.update(pokemonData)
    }

    fun import(data: String?): Boolean {
        return importer.import(repository, data)
    }

    fun export(): String? {
        exporter.shouldCompressData = repository.shouldCompressData()
        return exporter.export(repository)
    }

    fun getRandomPokemon(tabIndex: Int): PokemonData? {
        return repository.getRandomPokemonData(tabIndex)
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

    fun getPreferences(): Map<String, String> {
        return mapOf(

            App.PREFERENCES_GUIDE_SHOWN to repository.isGuideShown().toString(),
            App.PREFERENCES_AUTO_SORT to repository.shouldAutoSort().toString(),
            App.PREFERENCES_SORT_METHOD to repository.getSortMethod().toString()

        )
    }

    fun setShouldAutoSort(value: Boolean) {
        repository.setShouldAutoSort(value)
    }

    fun setDataCompression(value: Boolean) {
        repository.setDataCompression(value)
    }

    //endregion

}