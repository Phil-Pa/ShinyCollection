package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

class DataImporter {

    companion object {
        private const val defaultRegex =
            "PokemonData\\(name=([\\w+\\-\\d:]+), pokedexId=(\\d+), generation=(\\d), encounterNeeded=(\\d+), huntMethod=(\\w+), tabIndex=(\\d), internalId=(\\d+)\\)"
//        private const val compressedRegex = "\\(([\\w+\\-\\d:]+), (\\d+), (\\d), (\\d+), (\\w+), (\\d), (\\d+)\\)"
    }

    fun import(repository: PokemonRepository, data: String?): Boolean {
        if (data == null)
            return false

        val isCompressed = checkCompressed(data)

        val dataList = if (isCompressed)
            decompress(data).split("\n")
        else data.split("\n")

        val regex = Regex(defaultRegex)

        repository.deleteAll()

        for (dataString in dataList) {

            // TODO find better solution

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
            val tabIndex = match.groupValues[6].toInt()
            val internalId = match.groupValues[7].toInt()

            val pokemonData = PokemonData(
                name,
                pokedexId,
                generation,
                encounterNeeded,
                huntMethod,
                tabIndex
            )

            pokemonData.internalId = internalId

            repository.insert(pokemonData)
        }
        return true
    }

    private fun checkCompressed(data: String): Boolean {
        return !data.contains("PokemonData")
    }

    private fun decompress(str: String): String {
        if (str.isEmpty()) {
            return str
        }

        val b = ByteArray(str.length)
        var i = 0
        for (c in str)
            b[i++] = c.toByte()


        return "hi"
    }

}