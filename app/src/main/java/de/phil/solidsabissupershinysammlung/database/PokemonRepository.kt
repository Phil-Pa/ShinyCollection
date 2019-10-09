package de.phil.solidsabissupershinysammlung.database

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import de.phil.solidsabissupershinysammlung.model.PokemonData



class PokemonRepository(private val androidPokemonResources: IAndroidPokemonResources, application: Application) {

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

    fun getAllPokemonData(): List<PokemonData> {
        return GetAllPokemonDataAsyncTask(pokemonDao).execute().get()
    }

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

    class GetAllPokemonDataAsyncTask(private val pokemonDao: PokemonDao) : AsyncTask<Unit, Unit, List<PokemonData>>() {
        override fun doInBackground(vararg params: Unit?) : List<PokemonData> {
            return pokemonDao.getAllPokemonData()
        }
    }

}