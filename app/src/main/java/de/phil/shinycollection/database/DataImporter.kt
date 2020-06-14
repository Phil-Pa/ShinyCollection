package de.phil.shinycollection.database

import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.model.HuntMethod
import de.phil.shinycollection.model.PokemonData
import de.phil.shinycollection.model.PokemonEdition

class DataImporter {

    companion object {
        private const val defaultRegex =
            "PokemonData\\(name=([\\w+\\-\\d:]+), pokedexId=(\\d+), generation=(\\d), encounterNeeded=(\\d+), huntMethod=(\\w+), pokemonEdition=(\\w+), tabIndex=(\\d), internalId=(\\d+)\\)"
    }

    // TODO: what if an english app version imports german data?
    fun import(pokemonDao: PokemonDao, data: String?): Boolean {
        if (data == null)
            return false

        val isCompressed = GZIPCompression.isCompressed(data)

        return if (isCompressed)
            importCompressed(pokemonDao, data)
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

    private fun importCompressed(pokemonDao: PokemonDao, data: String): Boolean {

        val decompressed = GZIPCompression.decompress(data)

        val sb = StringBuilder(decompressed)

        val tempSb = StringBuilder(7)

        var index = 0
        for (i in sb.indices) {
            val chars = CharArray(7)
            sb.getChars(index, index + 7, chars, 0)

            tempSb.clear()

            for (char in chars)
                tempSb.append(char)

            val tempStr = tempSb.toString()

            val strTrue = tempStr == "Pokemon"

            if (strTrue && index != 0) {
                sb.insert(index, '\n')
                index += 10
            }

            index++

            if (index + 10 >= sb.length)
                break
        }

        val dataList = sb.toString().split('\n')

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

}