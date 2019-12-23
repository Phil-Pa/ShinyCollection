package de.phil.solidsabissupershinysammlung.database

import android.app.Application
import javax.inject.Inject

class DummyRepository @Inject constructor(application: Application) : PokemonRepository(object : IAndroidPokemonResources {
    override fun getPokemonNames(): List<String> {
        throw Exception()
    }

    override fun getPokedexIdByName(name: String): Int {
        throw Exception()
    }

    override fun getGenerationByName(name: String): Int {
        throw Exception()
    }

}, application)