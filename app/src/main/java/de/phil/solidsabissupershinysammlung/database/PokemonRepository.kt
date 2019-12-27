package de.phil.solidsabissupershinysammlung.database

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import de.phil.solidsabissupershinysammlung.ShinyPokemonApplication
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import javax.inject.Inject


open class PokemonRepository @Inject constructor (private val androidPokemonResources: IAndroidPokemonResources,
                                                  private val pokemonDao: PokemonDao) : IPokemonRepository {

    private val application = ShinyPokemonApplication.applicationContext()

    private val preferences: SharedPreferences = application.getSharedPreferences(application.packageName + App.PREFERENCES_NAME, Context.MODE_PRIVATE)

//    private val pokemonDao: PokemonDao =
//        PokemonRoomDatabase.instance(application.applicationContext).pokemonDao()

    override fun insert(pokemonData: PokemonData) {
        InsertAsyncTask(pokemonDao).execute(pokemonData)
    }

    override fun delete(pokemonData: PokemonData) {
        DeleteAsyncTask(pokemonDao).execute(pokemonData)
    }

    override fun deleteAll() {
        DeleteAllAsyncTask(pokemonDao).execute()
    }

    override fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData> {
        return GetAllPokemonDataFromTabIndexAsyncTask(pokemonDao).execute(tabIndex).get()
    }

    override fun getTotalNumberOfShinys(pokemonEdition: PokemonEdition): Int {
        return GetTotalNumberOfShinysAsyncTask(pokemonDao).execute(pokemonEdition.ordinal).get()
    }

    override fun getTotalNumberOfEggShinys(pokemonEdition: PokemonEdition): Int {
        return GetTotalNumberOfEggShinysAsyncTask(pokemonDao).execute(pokemonEdition.ordinal).get()
    }

    override fun getTotalNumberOfSosShinys(pokemonEdition: PokemonEdition): Int {
        return GetTotalNumberOfSosShinysAsyncTask(pokemonDao).execute(pokemonEdition.ordinal).get()
    }

    override fun getAverageSosEncounter(pokemonEdition: PokemonEdition): Float {
        return GetAverageSosEncounterAsyncTask(pokemonDao).execute(pokemonEdition.ordinal).get()
    }

    override fun getTotalNumberOfHatchedEggs(pokemonEdition: PokemonEdition): Int {
        return GetTotalNumberOfHatchedEggsAsyncTask(pokemonDao).execute(pokemonEdition.ordinal).get()
    }

    override fun getAverageEggsEncounter(pokemonEdition: PokemonEdition): Float {
        return GetAverageEggsEncounterAsyncTask(pokemonDao).execute(pokemonEdition.ordinal).get()
    }

    override fun getShinyListData(): LiveData<List<PokemonData>> {
        return GetShinyListDataAsyncTask(pokemonDao).execute().get()
    }

    override fun setGuideShown() {
        preferences.edit().putBoolean(App.PREFERENCES_GUIDE_SHOWN, true).apply()
    }

    override fun isGuideShown(): Boolean {
        return preferences.getBoolean(App.PREFERENCES_GUIDE_SHOWN, false)
    }

    override fun setSortMethod(sortMethod: PokemonSortMethod) {
        preferences.edit().putInt(App.PREFERENCES_SORT_METHOD, sortMethod.ordinal).apply()
    }

    override fun getSortMethod(): PokemonSortMethod {
        val intValue = preferences.getInt(App.PREFERENCES_SORT_METHOD, PokemonSortMethod.InternalId.ordinal)
        return PokemonSortMethod.fromInt(intValue) ?: throw Exception()
    }

    override fun setShouldAutoSort(value: Boolean) {
        preferences.edit().putBoolean(App.PREFERENCES_AUTO_SORT, value).apply()
    }

    override fun shouldAutoSort(): Boolean {
        return preferences.getBoolean(App.PREFERENCES_AUTO_SORT, false)
    }

    override fun setDataCompression(value: Boolean) {
        preferences.edit().putBoolean(App.PREFERENCES_COMPRESS_EXPORT_IMPORT, value).apply()
    }

    override fun shouldCompressData(): Boolean {
        return preferences.getBoolean(App.PREFERENCES_COMPRESS_EXPORT_IMPORT, false)
    }

    override fun getPokemonNames(): List<String> {
        return androidPokemonResources.getPokemonNames()
    }

    override fun getPokedexIdByName(name: String): Int {
        return androidPokemonResources.getPokedexIdByName(name)
    }

    override fun getGenerationByName(name: String): Int {
        return androidPokemonResources.getGenerationByName(name)
    }

    override fun getMaxInternalId(): Int {
        return GetMaxInternalIdAsyncTask(pokemonDao).execute().get()
    }

    override fun getRandomPokemonData(tabIndex: Int, pokemonEdition: PokemonEdition): PokemonData? {
        return GetRandomPokemonDataAsyncTask(pokemonDao).execute(tabIndex, pokemonEdition.ordinal).get()
    }

    override fun update(pokemonData: PokemonData) {
        UpdateAsyncTask(pokemonDao).execute(pokemonData)
    }

    override fun getAllPokemonData(): List<PokemonData> {
        return GetAllPokemonDataAsyncTask(pokemonDao).execute().get()
    }

    override fun setPokemonEdition(pokemonEdition: PokemonEdition) {
        preferences.edit().putInt(App.PREFERENCES_POKEMON_EDITION, pokemonEdition.ordinal).apply()
    }

    override fun getPokemonEdition(): PokemonEdition {
        val ordinal = preferences.getInt(App.PREFERENCES_POKEMON_EDITION, PokemonEdition.USUM.ordinal)
        return PokemonEdition.fromInt(ordinal)!!
    }

    //region async tasks

    class GetAllPokemonDataAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, List<PokemonData>>() {
        override fun doInBackground(vararg params: Unit?): List<PokemonData> {
            return pokemonDao.getAllPokemonData()
        }
    }

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

    class GetTotalNumberOfShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            return pokemonDao.getTotalNumberOfShinys(params[0]!!)
        }
    }

    class GetTotalNumberOfEggShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            return pokemonDao.getTotalNumberOfEggShinys(params[0]!!)
        }
    }

    class GetTotalNumberOfSosShinysAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            return pokemonDao.getTotalNumberOfSosShinys(params[0]!!)
        }
    }

    class GetAverageSosEncounterAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, Float>() {
        override fun doInBackground(vararg params: Int?): Float {
            return pokemonDao.getAverageSosCount(params[0]!!)
        }
    }

    class GetTotalNumberOfHatchedEggsAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            return pokemonDao.getTotalEggsCount(params[0]!!)
        }
    }

    class GetAverageEggsEncounterAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Int, Unit, Float>() {
        override fun doInBackground(vararg params: Int?): Float {
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
            return pokemonDao.getRandomPokemonData(params[0]!!, params[1]!!)
        }
    }

    //endregion

}