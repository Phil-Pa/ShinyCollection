package de.phil.shinycollection.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.database.DataManager
import de.phil.shinycollection.database.PokemonDao
import de.phil.shinycollection.database.PokemonDatabase
import de.phil.shinycollection.model.PokemonData
import de.phil.shinycollection.model.PokemonEdition
import de.phil.shinycollection.model.PokemonSortMethod
import de.phil.shinycollection.model.UpdateStatisticsData
import de.phil.shinycollection.utils.round

fun AndroidViewModel.getPreferences(): SharedPreferences {
    return PokemonDatabase.preferences(getApplication())
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val pokemonDao: PokemonDao =
        PokemonDatabase.instance(application.applicationContext).pokemonDao()
    var currentTheme: String? = null

    private val pokemonEditionLiveData = MutableLiveData<PokemonEdition>()
    private val dataManager =
        DataManager()

    fun updatePokemon(pokemonData: PokemonData) {
        pokemonDao.updatePokemon(pokemonData)
    }

    fun import(data: String?, action: (Boolean) -> Unit) {
        val value = dataManager.import(getApplication(), pokemonDao, data)
        action(value)
    }

    fun export(shouldCompressData: Boolean, action: (String?) -> Unit) {
        val value = dataManager.export(pokemonDao, shouldCompressData)
        action(value)
    }

    fun getRandomPokemon(tabIndex: Int): PokemonData? {
        return pokemonDao.getRandomPokemonData(tabIndex, getPokemonEdition().ordinal)
    }

    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData> {
        return pokemonDao.getAllPokemonDataFromTabIndex(tabIndex).filter { it.pokemonEdition == getPokemonEdition() }
    }

    fun deletePokemon(pokemonToDelete: PokemonData) {
        pokemonDao.deletePokemonFromDatabase(pokemonToDelete)
    }

    fun getStatisticsData(): UpdateStatisticsData {
        val totalShinys = pokemonDao.getTotalNumberOfShinys(getPokemonEdition().ordinal)
        val totalEggShinys = pokemonDao.getTotalNumberOfEggShinys(getPokemonEdition().ordinal)
        val totalSosShinys = pokemonDao.getTotalNumberOfSosShinys(getPokemonEdition().ordinal)
        val averageSos = pokemonDao.getAverageSosCount(getPokemonEdition().ordinal).round(2)
        val totalEggs = pokemonDao.getTotalEggsCount(getPokemonEdition().ordinal)
        val averageEggs = pokemonDao.getAverageEggsCount(getPokemonEdition().ordinal).round(2)

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
        return pokemonDao.getShinyListData()
    }

    //region preferences

    fun setGuideShown() {
        getPreferences().edit().putBoolean(ShinyPokemonApplication.PREFERENCES_GUIDE_SHOWN, true).apply()
    }

    fun isGuideShown(): Boolean {
        return getPreferences().getBoolean(ShinyPokemonApplication.PREFERENCES_GUIDE_SHOWN, false)
    }

    fun setSortMethod(pokemonSortMethod: PokemonSortMethod) {
        getPreferences().edit().putInt(ShinyPokemonApplication.PREFERENCES_SORT_METHOD, pokemonSortMethod.ordinal).apply()
    }

    fun getSortMethod(): PokemonSortMethod {
        val intValue = getPreferences().getInt(ShinyPokemonApplication.PREFERENCES_SORT_METHOD, PokemonSortMethod.InternalId.ordinal)
        return PokemonSortMethod.fromInt(intValue)!!
    }

    fun shouldAutoSort(): Boolean {
        return getPreferences().getBoolean(ShinyPokemonApplication.PREFERENCES_AUTO_SORT, false)
    }

    fun setPokemonEdition(pokemonEdition: PokemonEdition) {
        getPreferences().edit().putInt(ShinyPokemonApplication.PREFERENCES_POKEMON_EDITION, pokemonEdition.ordinal).apply()
        pokemonEditionLiveData.value = pokemonEdition
    }

    fun getPokemonEdition(): PokemonEdition {
        val ordinal = getPreferences().getInt(ShinyPokemonApplication.PREFERENCES_POKEMON_EDITION, PokemonEdition.USUM.ordinal)
        return PokemonEdition.fromInt(ordinal)!!
    }

    fun getPokemonEditionLiveData(): LiveData<PokemonEdition> {
        return pokemonEditionLiveData
    }

    fun increasePokemonEncounter(pokemonData: PokemonData) {
        pokemonData.encounterNeeded++
        updatePokemon(pokemonData)
    }

    //endregion

}