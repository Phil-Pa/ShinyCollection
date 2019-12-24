package de.phil.solidsabissupershinysammlung.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import de.phil.solidsabissupershinysammlung.database.DataExporter
import de.phil.solidsabissupershinysammlung.database.DataImporter
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.model.UpdateStatisticsData
import de.phil.solidsabissupershinysammlung.utils.round
import javax.inject.Inject

class MainViewModel @Inject
constructor(private val pokemonRepository: IPokemonRepository) : ViewModel() {

    private val exporter = DataExporter()
    private val importer = DataImporter()

    fun addPokemon(pokemonData: PokemonData) {
        pokemonRepository.insert(pokemonData)
    }

    fun updatePokemon(pokemonData: PokemonData) {
        pokemonRepository.update(pokemonData)
    }

    fun import(data: String?): Boolean {
        return importer.import(pokemonRepository, data)
    }

    fun export(): String? {
        exporter.shouldCompressData = pokemonRepository.shouldCompressData()
        return exporter.export(pokemonRepository)
    }

    fun getRandomPokemon(tabIndex: Int): PokemonData? {
        return pokemonRepository.getRandomPokemonData(tabIndex)
    }

    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData> {
        return pokemonRepository.getAllPokemonDataFromTabIndex(tabIndex)
    }

    fun deletePokemon(pokemonToDelete: PokemonData) {
        pokemonRepository.delete(pokemonToDelete)
    }

    fun getStatisticsData(): UpdateStatisticsData {
        val totalShinys = pokemonRepository.getTotalNumberOfShinys()
        val totalEggShinys = pokemonRepository.getTotalNumberOfEggShinys()
        val totalSosShinys = pokemonRepository.getTotalNumberOfSosShinys()
        val averageSos = pokemonRepository.getAverageSosEncounter().round(2)
        val totalEggs = pokemonRepository.getTotalNumberOfHatchedEggs()
        val averageEggs = pokemonRepository.getAverageEggsEncounter().round(2)

        return UpdateStatisticsData(
            totalShinys,
            totalEggShinys,
            totalSosShinys,
            averageSos,
            totalEggs,
            averageEggs
        )
    }

    fun getShinyListData(): LiveData<List<PokemonData>> {
        return pokemonRepository.getShinyListData()
    }

    //region preferences

    fun setGuideShown() {
        pokemonRepository.setGuideShown()
    }

    fun isGuideShown(): Boolean {
        return pokemonRepository.isGuideShown()
    }

    fun setSortMethod(pokemonSortMethod: PokemonSortMethod) {
        pokemonRepository.setSortMethod(pokemonSortMethod)
    }

    fun getSortMethod(): PokemonSortMethod {
        return pokemonRepository.getSortMethod()
    }

    fun shouldAutoSort(): Boolean {
        return pokemonRepository.shouldAutoSort()
    }

    fun setShouldAutoSort(value: Boolean) {
        pokemonRepository.setShouldAutoSort(value)
    }

    fun setDataCompression(value: Boolean) {
        pokemonRepository.setDataCompression(value)
    }

    //endregion

}