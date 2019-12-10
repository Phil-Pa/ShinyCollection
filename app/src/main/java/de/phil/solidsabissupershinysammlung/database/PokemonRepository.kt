package de.phil.solidsabissupershinysammlung.database

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod


open class PokemonRepository(private val androidPokemonResources: IAndroidPokemonResources, application: Application) {

    private val preferences: SharedPreferences = application.getSharedPreferences(application.packageName + App.PREFERENCES_NAME, Context.MODE_PRIVATE)
    private var currentEdition = SharedPreferencesPokemonEditionLiveData(preferences, App.PREFERENCES_POKEMON_EDITION, PokemonEdition.SWSH)

    private val pokemonDao: PokemonDao =
        PokemonRoomDatabase.instance(application.applicationContext).pokemonDao()

    fun insert(pokemonData: PokemonData) {
        InsertAsyncTask(pokemonDao).execute(pokemonData)
    }

    fun delete(pokemonData: PokemonData) {
        DeleteAsyncTask(pokemonDao).execute(pokemonData)
    }

    fun deleteAll() {
        DeleteAllAsyncTask(pokemonDao).execute()
    }

    fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData> {
        return GetAllPokemonDataFromTabIndexAsyncTask(pokemonDao).execute(tabIndex).get()
    }

    fun getTotalNumberOfShinys(pokemonEdition: PokemonEdition): Int {
        return GetTotalNumberOfShinysAsyncTask(pokemonDao).execute(pokemonEdition).get()
    }

    fun getTotalNumberOfEggShinys(pokemonEdition: PokemonEdition): Int {
        return GetTotalNumberOfEggShinysAsyncTask(pokemonDao).execute(pokemonEdition).get()
    }

    fun getTotalNumberOfSosShinys(pokemonEdition: PokemonEdition): Int {
        return GetTotalNumberOfSosShinysAsyncTask(pokemonDao).execute(pokemonEdition).get()
    }

    fun getAverageSosEncounter(pokemonEdition: PokemonEdition): Float {
        return GetAverageSosEncounterAsyncTask(pokemonDao).execute(pokemonEdition).get()
    }

    fun getTotalNumberOfHatchedEggs(pokemonEdition: PokemonEdition): Int {
        return GetTotalNumberOfHatchedEggsAsyncTask(pokemonDao).execute(pokemonEdition).get()
    }

    fun getAverageEggsEncounter(pokemonEdition: PokemonEdition): Float {
        return GetAverageEggsEncounterAsyncTask(pokemonDao).execute(pokemonEdition).get()
    }

    fun getShinyListData(): LiveData<List<PokemonData>> {
        return GetShinyListDataAsyncTask(pokemonDao).execute().get()
    }

    fun setPokemonEdition(edition: PokemonEdition) {
        preferences.edit().putInt(App.PREFERENCES_POKEMON_EDITION, edition.ordinal).apply()
    }

    fun getPokemonEdition(): SharedPreferencesPokemonEditionLiveData {
        return currentEdition
    }

    fun setGuideShown() {
        preferences.edit().putBoolean(App.PREFERENCES_GUIDE_SHOWN, true).apply()
    }

    fun isGuideShown(): Boolean {
        return preferences.getBoolean(App.PREFERENCES_GUIDE_SHOWN, false)
    }

    fun setSortMethod(sortMethod: PokemonSortMethod) {
        preferences.edit().putInt(App.PREFERENCES_SORT_METHOD, sortMethod.ordinal).apply()
    }

    fun getSortMethod(): PokemonSortMethod {
        val intValue = preferences.getInt(App.PREFERENCES_SORT_METHOD, PokemonSortMethod.InternalId.ordinal)
        return PokemonSortMethod.fromInt(intValue) ?: throw Exception()
    }

    fun setShouldAutoSort(value: Boolean) {
        preferences.edit().putBoolean(App.PREFERENCES_AUTO_SORT, value).apply()
    }

    fun shouldAutoSort(): Boolean {
        return preferences.getBoolean(App.PREFERENCES_AUTO_SORT, false)
    }

    fun setDataCompression(value: Boolean) {
        preferences.edit().putBoolean(App.PREFERENCES_COMPRESS_EXPORT_IMPORT, value).apply()
    }

    fun shouldCompressData(): Boolean {
        return preferences.getBoolean(App.PREFERENCES_COMPRESS_EXPORT_IMPORT, false)
    }

    fun getPokemonNames(): List<String> {
        return androidPokemonResources.getPokemonNames()
    }

    fun getPokedexIdByName(name: String): Int {
        return androidPokemonResources.getPokedexIdByName(name)
    }

    fun getGenerationByName(name: String): Int {
        return androidPokemonResources.getGenerationByName(name)
    }

    fun getMaxInternalId(): Int {
        return GetMaxInternalIdAsyncTask(pokemonDao).execute().get()
    }

    fun getRandomPokemonData(tabIndex: Int): PokemonData? {
        return GetRandomPokemonDataAsyncTask(pokemonDao).execute(tabIndex).get()
    }

    fun update(pokemonData: PokemonData) {
        UpdateAsyncTask(pokemonDao).execute(pokemonData)
    }

    fun isOnlyCurrentEdition(): Boolean {
        return preferences.getBoolean(App.PREFERENCES_SHOW_ONLY_CURRENT_EDITION, false)
    }

    //region async tasks

    class InsertAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonData, Unit, Unit>() {
        override fun doInBackground(vararg params: PokemonData?) {
            for (param in params)
                if (param != null)
                    pokemonDao.addPokemon(param)
        }
    }

    class UpdateAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonData, Unit, Unit>() {
        override fun doInBackground(vararg params: PokemonData?) {
            for (param in params)
                if (param != null)
                    pokemonDao.updatePokemon(param)
        }
    }

    class DeleteAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonData, Unit, Unit>() {
        override fun doInBackground(vararg params: PokemonData?) {
            for (param in params)
                if (param != null)
                    pokemonDao.deletePokemonFromDatabase(param)
        }
    }

    class DeleteAllAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, Unit>() {
        override fun doInBackground(vararg params: Unit?) {
            pokemonDao.deleteAllPokemonInDatabase()
        }
    }

    class GetTotalNumberOfShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonEdition, Unit, Int>() {
        override fun doInBackground(vararg params: PokemonEdition?): Int {
            return pokemonDao.getTotalNumberOfShinys(params[0]!!)
        }
    }

    class GetTotalNumberOfEggShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonEdition, Unit, Int>() {
        override fun doInBackground(vararg params: PokemonEdition?): Int {
            return pokemonDao.getTotalNumberOfEggShinys(params[0]!!)
        }
    }

    class GetTotalNumberOfSosShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonEdition, Unit, Int>() {
        override fun doInBackground(vararg params: PokemonEdition?): Int {
            return pokemonDao.getTotalNumberOfSosShinys(params[0]!!)
        }
    }

    class GetAverageSosEncounterAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonEdition, Unit, Float>() {
        override fun doInBackground(vararg params: PokemonEdition?): Float {
            return pokemonDao.getAverageSosCount(params[0]!!)
        }
    }

    class GetTotalNumberOfHatchedEggsAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonEdition, Unit, Int>() {
        override fun doInBackground(vararg params: PokemonEdition?): Int {
            return pokemonDao.getTotalEggsCount(params[0]!!)
        }
    }

    class GetAverageEggsEncounterAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonEdition, Unit, Float>() {
        override fun doInBackground(vararg params: PokemonEdition?): Float {
            return pokemonDao.getAverageEggsCount(params[0]!!)
        }
    }

    class GetShinyListDataAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, LiveData<List<PokemonData>>>() {
        override fun doInBackground(vararg params: Unit?): LiveData<List<PokemonData>> {
            return pokemonDao.getShinyListData()
        }
    }

    class GetAllPokemonDataFromTabIndexAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, List<PokemonData>>() {
        override fun doInBackground(vararg params: Int?): List<PokemonData> {
            val tabIndex = params[0] ?: throw Exception()
            return pokemonDao.getAllPokemonDataFromTabIndex(tabIndex)
        }
    }

    class GetMaxInternalIdAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, Int>() {
        override fun doInBackground(vararg params: Unit?): Int {
            return pokemonDao.getMaxInternalId()
        }
    }

    class GetRandomPokemonDataAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, PokemonData?>() {
        override fun doInBackground(vararg params: Int?): PokemonData? {
            return pokemonDao.getRandomPokemonData(params[0]!!)
        }
    }

    //endregion

}