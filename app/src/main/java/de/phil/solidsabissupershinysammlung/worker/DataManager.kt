package de.phil.solidsabissupershinysammlung.worker

import de.phil.solidsabissupershinysammlung.database.DataExporter
import de.phil.solidsabissupershinysammlung.database.DataImporter
import de.phil.solidsabissupershinysammlung.database.IPokemonRepository

class DataManager : BackgroundDataManager {

    private val dataImporter =
        DataImporter()
    private val dataExporter =
        DataExporter()

    override fun import(pokemonRepository: IPokemonRepository, data: String?): Boolean {
        return dataImporter.import(pokemonRepository, data)
    }

    override fun export(pokemonRepository: IPokemonRepository): String? {
        return dataExporter.export(pokemonRepository)
    }

}