package de.phil.shinycollection.database

import android.app.Application

class DataManager {

    private val dataImporter = DataImporter()
    private val dataExporter = DataExporter()

    fun import(application: Application, pokemonDao: PokemonDao, data: String?): Boolean {
        return dataImporter.import(application, pokemonDao, data)
    }

    fun export(pokemonDao: PokemonDao, shouldCompressData: Boolean): String? {
        return dataExporter.export(pokemonDao, shouldCompressData)
    }

}