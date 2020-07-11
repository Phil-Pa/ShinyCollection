package de.phil.shinycollection.database

class DataExporter {

    fun export(pokemonDao: PokemonDao, shouldCompressData: Boolean): String? {
        val pokemonList = pokemonDao.getAllPokemonData()

        if (pokemonList.isEmpty())
            return null

        val sb = StringBuilder()

        pokemonList.forEach {
            val pokemonDataAsText =
                if (shouldCompressData)
                    it.toShortString()
                else
                    it.toString()

            sb.append(pokemonDataAsText).append("\n")
        }

        val str = sb.toString()

        return if (shouldCompressData)
            GZIPCompression.compress(str)
        else str
    }
}

