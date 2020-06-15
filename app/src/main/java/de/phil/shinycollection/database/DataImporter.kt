package de.phil.shinycollection.database

import android.app.Application
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.model.HuntMethod
import de.phil.shinycollection.model.PokemonData
import de.phil.shinycollection.model.PokemonEdition

class DataImporter {

    companion object {
        private const val defaultRegex =
            "PokemonData\\(name=([\\w+\\-\\d:]+), pokedexId=(\\d+), generation=(\\d), encounterNeeded=(\\d+), huntMethod=(\\w+), pokemonEdition=(\\w+), tabIndex=(\\d), internalId=(\\d+)\\)"
    }

    fun import(application: Application, pokemonDao: PokemonDao, data: String?): Boolean {
        if (data == null)
            return false

        val isCompressed = GZIPCompression.isCompressed(data)

        return if (isCompressed)
            importCompressed(application, pokemonDao, data)
        else
            importNormal(pokemonDao, data)


    }

    private fun importNormal(pokemonDao: PokemonDao, data: String): Boolean {
        val dataList = data.split("\n")

        val regex = Regex(defaultRegex)

        pokemonDao.deleteAllPokemonInDatabase()

        var importSuccess = true

        for (dataString in dataList) {

            if (dataString == "\n" || dataString.isEmpty() || dataString.isBlank())
                continue

            if (!regex.matches(dataString)) {
                return false
            }

            val match: MatchResult = regex.matchEntire(dataString) ?: return false

            val name = match.groupValues[1]
            val pokedexId = match.groupValues[2].toInt()
            val generation = match.groupValues[3].toInt()
            val encounterNeeded = match.groupValues[4].toInt()
            val huntMethod = HuntMethod.valueOf(match.groupValues[5])
            val pokemonEdition = PokemonEdition.valueOf(match.groupValues[6])
            val tabIndex = match.groupValues[7].toInt()
            val internalId = match.groupValues[8].toInt()

            if (tabIndex >= ShinyPokemonApplication.NUM_TAB_VIEWS)
                importSuccess = false

            val pokemonData = PokemonData(
                name,
                pokedexId,
                generation,
                encounterNeeded,
                huntMethod,
                pokemonEdition,
                tabIndex
            )

            pokemonData.internalId = internalId
            pokemonDao.addPokemon(pokemonData)
        }

        return  importSuccess
    }

    private fun importCompressed(application: Application, pokemonDao: PokemonDao, data: String): Boolean {

        val androidPokemonResources = PokemonDatabase.androidPokemonResources(application)

        val decompressed = GZIPCompression.decompress(data)

        val numDataEntries = decompressed.length / PokemonData.SHORT_DATA_STRING_LENGTH

        pokemonDao.deleteAllPokemonInDatabase()

        for (i in 0 until numDataEntries) {
            val startIndex = i * PokemonData.SHORT_DATA_STRING_LENGTH

            val pokedexId = decompressed.substring(startIndex, startIndex + 3).toInt()
            val generation = (decompressed[startIndex + 3] - '0')
            val encounters = decompressed.substring(startIndex + 4, startIndex + 9).toInt()
            val huntMethod = HuntMethod.fromInt(decompressed.substring(startIndex + 9, startIndex + 11).toInt()) ?: return false
            val pokemonEdition = PokemonEdition.fromInt(decompressed[startIndex + 11] - '0') ?: return false
            val tabIndex = (decompressed[startIndex + 12] - '0')
            val formFlag = (decompressed[startIndex + 13] - '0')
            val internalId = decompressed.substring(startIndex + 14, startIndex + 17).toInt()

            var name = androidPokemonResources.getNameByPokedexId(pokedexId)

            if (formFlag == 1)
                name += ShinyPokemonApplication.ALOLA_EXTENSION
            else if (formFlag == 2)
                name += ShinyPokemonApplication.GALAR_EXTENSION

            val pokemonData = PokemonData(
                name,
                pokedexId,
                generation,
                encounters,
                huntMethod,
                pokemonEdition,
                tabIndex
            )

            pokemonData.internalId = internalId

            try {
                pokemonDao.addPokemon(pokemonData)
            } catch (e: Exception) {
                val fjkd = 232
            }
        }

        return true
    }

}