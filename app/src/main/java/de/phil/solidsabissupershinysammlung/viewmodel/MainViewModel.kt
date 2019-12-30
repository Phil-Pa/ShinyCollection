package de.phil.solidsabissupershinysammlung.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.phil.solidsabissupershinysammlung.database.DataManager
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.model.UpdateStatisticsData
import de.phil.solidsabissupershinysammlung.utils.round
import javax.inject.Inject

class MainViewModel @Inject
constructor(private val pokemonRepository: IPokemonRepository) : ViewModel() {

    private val dataManager = DataManager()

    var currentTheme: String? = null

    private val pokemonEditionLiveData = MutableLiveData<PokemonEdition>()

    fun addPokemon(pokemonData: PokemonData) {
        pokemonRepository.insert(pokemonData)
    }

    fun updatePokemon(pokemonData: PokemonData) {
        pokemonRepository.update(pokemonData)
    }

    fun import(data: String?): Boolean {
        return dataManager.import(pokemonRepository, data)
    }

    fun export(): String? {
        val shouldCompressData = pokemonRepository.shouldCompressData()
        return dataManager.export(shouldCompressData, pokemonRepository)
    }

    fun getRandomPokemon(tabIndex: Int): PokemonData? {
        return pokemonRepository.getRandomPokemonData(tabIndex, getPokemonEdition())
    }

    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData> {
        return pokemonRepository.getAllPokemonDataFromTabIndex(tabIndex).filter { it.pokemonEdition == getPokemonEdition() }
    }

    fun deletePokemon(pokemonToDelete: PokemonData) {
        pokemonRepository.delete(pokemonToDelete)
    }

    fun getStatisticsData(): UpdateStatisticsData {
        val totalShinys = pokemonRepository.getTotalNumberOfShinys(getPokemonEdition())
        val totalEggShinys = pokemonRepository.getTotalNumberOfEggShinys(getPokemonEdition())
        val totalSosShinys = pokemonRepository.getTotalNumberOfSosShinys(getPokemonEdition())
        val averageSos = pokemonRepository.getAverageSosEncounter(getPokemonEdition()).round(2)
        val totalEggs = pokemonRepository.getTotalNumberOfHatchedEggs(getPokemonEdition())
        val averageEggs = pokemonRepository.getAverageEggsEncounter(getPokemonEdition()).round(2)

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

    fun setPokemonEdition(pokemonEdition: PokemonEdition) {
        pokemonRepository.setPokemonEdition(pokemonEdition)
        pokemonEditionLiveData.value = pokemonEdition
    }

    fun getPokemonEdition(): PokemonEdition {
        return pokemonRepository.getPokemonEdition()
    }

    fun getPokemonEditionLiveData(): LiveData<PokemonEdition> {
        return pokemonEditionLiveData
    }

    //endregion

}