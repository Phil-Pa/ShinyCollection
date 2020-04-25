package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.database.DataExporter
import de.phil.solidsabissupershinysammlung.database.DataImporter
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository

class DataManager {

    private val dataImporter =
        DataImporter()
    private val dataExporter =
        DataExporter()

    fun import(pokemonRepository: IPokemonRepository, data: String?): Boolean {
        return dataImporter.import(pokemonRepository, data)
    }

    fun export(pokemonRepository: IPokemonRepository): String? {
        return dataExporter.export(pokemonRepository)
    }

}