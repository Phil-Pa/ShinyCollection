package de.phil.solidsabissupershinysammlung.database

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod


class PokemonRepository(private val androidPokemonResources: IAndroidPokemonResources, application: Application) {

    private val preferences: SharedPreferences = application.getSharedPreferences(application.packageName + "." + App.PREFERENCES_NAME, Context.MODE_PRIVATE)

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

    fun getAllPokemonData(): LiveData<List<PokemonData>> {
        return GetAllPokemonDataAsyncTask(pokemonDao).execute().get()
    }

    fun getAllPokemonDataFromTabIndex(tabIndex: Int): LiveData<List<PokemonData>> {
        return GetAllPokemonDataFromTabIndexAsyncTask(pokemonDao).execute(tabIndex).get()
    }

    fun getTotalNumberOfShinys(): Int {
        return GetTotalNumberOfShinysAsyncTask(pokemonDao).execute().get()
    }

    fun getTotalNumberOfEggShinys(): Int {
        return GetTotalNumberOfEggShinysAsyncTask(pokemonDao).execute().get()
    }

    fun getTotalNumberOfSosShinys(): Int {
        return GetTotalNumberOfSosShinysAsyncTask(pokemonDao).execute().get()
    }

    fun getAverageSosEncounter(): Float {
        return GetAverageSosEncounterAsyncTask(pokemonDao).execute().get()
    }

    fun getTotalNumberOfHatchedEggs(): Int {
        return GetTotalNumberOfHatchedEggsAsyncTask(pokemonDao).execute().get()
    }

    fun getAverageEggsEncounter(): Float {
        return GetAverageEggsEncounterAsyncTask(pokemonDao).execute().get()
    }

    fun getShinyListData(): LiveData<List<PokemonData>> {
        return GetShinyListDataAsyncTask(pokemonDao).execute().get()
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

    fun shouldAutoSort(): Boolean {
        return preferences.getBoolean(App.PREFERENCES_AUTO_SORT, false)
    }

    //region async tasks

    class InsertAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<PokemonData, Unit, Unit>() {
        override fun doInBackground(vararg params: PokemonData?) {
            for (param in params)
                if (param != null)
                    pokemonDao.addPokemon(param)
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

    class GetAllPokemonDataAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, LiveData<List<PokemonData>>>() {
        override fun doInBackground(vararg params: Unit?) : LiveData<List<PokemonData>> {
            return pokemonDao.getAllPokemonData()
        }
    }

    class GetTotalNumberOfShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, Int>() {
        override fun doInBackground(vararg params: Unit?): Int {
            return pokemonDao.getTotalNumberOfShinys()
        }
    }

    class GetTotalNumberOfEggShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, Int>() {
        override fun doInBackground(vararg params: Unit?): Int {
            return pokemonDao.getTotalNumberOfEggShinys()
        }
    }

    class GetTotalNumberOfSosShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, Int>() {
        override fun doInBackground(vararg params: Unit?): Int {
            return pokemonDao.getTotalNumberOfSosShinys()
        }
    }

    class GetAverageSosEncounterAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, Float>() {
        override fun doInBackground(vararg params: Unit?): Float {
            return pokemonDao.getAverageSosCount()
        }
    }

    class GetTotalNumberOfHatchedEggsAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, Int>() {
        override fun doInBackground(vararg params: Unit?): Int {
            return pokemonDao.getTotalEggsCount()
        }
    }

    class GetAverageEggsEncounterAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, Float>() {
        override fun doInBackground(vararg params: Unit?): Float {
            return pokemonDao.getAverageEggsCount()
        }
    }

    class GetShinyListDataAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, LiveData<List<PokemonData>>>() {
        override fun doInBackground(vararg params: Unit?): LiveData<List<PokemonData>> {
            return pokemonDao.getShinyListData()
        }
    }

    class GetAllPokemonDataFromTabIndexAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, LiveData<List<PokemonData>>>() {
        override fun doInBackground(vararg params: Int?): LiveData<List<PokemonData>> {
            val tabIndex = params[0] ?: throw Exception()
            return pokemonDao.getAllPokemonDataFromTabIndex(tabIndex)
        }
    }

    //endregion

}