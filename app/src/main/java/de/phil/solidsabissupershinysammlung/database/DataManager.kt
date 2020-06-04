package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.database.DataExporter
import de.phil.solidsabissupershinysammlung.database.DataImporter
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository

class DataManager {

    private val dataImporter =
        DataImporter()
    private val dataExporter =
        DataExporter()

    fun import(pokemonDao: PokemonDao, data: String?): Boolean {
        return dataImporter.import(pokemonDao, data)
    }

    fun export(pokemonDao: PokemonDao): String? {
        return dataExporter.export(pokemonDao)
    }

}