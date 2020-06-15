package de.phil.shinycollection.database

class DataExporter {

    fun export(pokemonDao: PokemonDao, shouldCompressData: Boolean): String? {

        val pokemonList = pokemonDao.getAllPokemonData()

        if (pokemonList.isEmpty())
            return null

        val sb = StringBuilder()

        // TODO: make data shorter and change implementation in importer
        pokemonList.forEach { sb.append(if (shouldCompressData) it.toShortString() else it.toString()).append("\n") }

//        pokemonList.forEach { sb.append(it.toString()).append("\n") }

        val str = sb.toString()

        return if (shouldCompressData)
            GZIPCompression.compress(str)
        else str
    }
}

