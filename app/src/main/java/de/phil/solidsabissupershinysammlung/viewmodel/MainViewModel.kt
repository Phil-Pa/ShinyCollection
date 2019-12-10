package de.phil.solidsabissupershinysammlung.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.database.*
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
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

    fun getPokemonEdition(): SharedPreferencesPokemonEditionLiveData {
        return repository.getPokemonEdition()
    }

    fun setPokemonEdition(edition: PokemonEdition) {
        repository.setPokemonEdition(edition)
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
        var result = repository.getAllPokemonDataFromTabIndex(tabIndex)

        if (isOnlyCurrentEdition())
            result = result.filter { it.pokemonEdition == getPokemonEdition().value!! }

        return result
    }

    fun deletePokemon(pokemonToDelete: PokemonData) {
        repository.delete(pokemonToDelete)
    }

    fun getStatisticsData(): UpdateStatisticsData {
        val pokemonEdition = getPokemonEdition().value!!
        if (isOnlyCurrentEdition()) {
            val totalShinys = repository.getTotalNumberOfShinys(pokemonEdition)
            val totalEggShinys = repository.getTotalNumberOfEggShinys(pokemonEdition)
            val totalSosShinys = repository.getTotalNumberOfSosShinys(pokemonEdition)
            val averageSos = repository.getAverageSosEncounter(pokemonEdition).round(2)
            val totalEggs = repository.getTotalNumberOfHatchedEggs(pokemonEdition)
            val averageEggs = repository.getAverageEggsEncounter(pokemonEdition).round(2)
            return UpdateStatisticsData(totalShinys, totalEggShinys, totalSosShinys, averageSos, totalEggs, averageEggs)
        } else {
            val editions = PokemonEdition.getPokemonEditionUpTo(pokemonEdition)
            var totalShinys = 0
            var totalEggShinys = 0
            var totalSosShinys = 0
            var averageSos = 0f
            var totalEggs = 0
            var averageEggs = 0f

            for (edition in editions) {
                totalShinys += repository.getTotalNumberOfShinys(edition)
                totalEggShinys += repository.getTotalNumberOfEggShinys(edition)
                totalSosShinys += repository.getTotalNumberOfSosShinys(edition)
                averageSos += repository.getAverageSosEncounter(edition)
                totalEggs += repository.getTotalNumberOfHatchedEggs(edition)
                averageEggs += repository.getAverageEggsEncounter(edition)
            }

            // we summed up the avg, now divide by number of summands
            averageEggs /= editions.size.toFloat()
            averageSos /= editions.size.toFloat()

            return UpdateStatisticsData(totalShinys, totalEggShinys, totalSosShinys, averageSos, totalEggs, averageEggs)
        }
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

    fun isOnlyCurrentEdition(): Boolean {
        return repository.isOnlyCurrentEdition()
    }

    //endregion

}