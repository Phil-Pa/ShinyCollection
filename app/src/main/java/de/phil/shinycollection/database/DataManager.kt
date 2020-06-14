package de.phil.shinycollection.database

class DataManager {

    private val dataImporter =
        DataImporter()
    private val dataExporter =
        DataExporter()

    fun import(pokemonDao: PokemonDao, data: String?): Boolean {
        return dataImporter.import(pokemonDao, data)
    }

    fun export(
        pokemonDao: PokemonDao,
        shouldCompressData: Boolean
    ): String? {
        return dataExporter.export(pokemonDao, shouldCompressData)
    }

}