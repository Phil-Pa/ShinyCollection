package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

class DataImporter {

    companion object {
        private const val defaultRegex =
            "PokemonData\\(name=([\\w+\\-\\d:]+), pokedexId=(\\d+), generation=(\\d), encounterNeeded=(\\d+), huntMethod=(\\w+), pokemonEdition=(\\w+), tabIndex=(\\d), internalId=(\\d+)\\)"
        private const val compressedRegex = "\\((\\d+), (\\d), (\\d+), (\\w+), (\\w+), (\\d), (\\d+)\\)"
    }

    fun import(repository: IPokemonRepository, data: String?): Boolean {
        if (data == null)
            return false

        val isCompressed = checkCompressed(data)

        val dataList = data.split("\n")

        val regex = Regex(if (isCompressed) compressedRegex else defaultRegex)

        repository.deleteAll()

        for (dataString in dataList) {

            // TODO find better solution

            if (dataString == "\n" || dataString.isEmpty() || dataString.isBlank())
                continue

            if (!regex.matches(dataString)) {
                return false
            }

            val match: MatchResult = regex.matchEntire(dataString) ?: return false

            if (isCompressed) {
                val pokedexId = match.groupValues[1].toInt()
                val name = repository.getNameByPokedexId(pokedexId)
                val generation = match.groupValues[2].toInt()
                val encounterNeeded = match.groupValues[3].toInt()
                val huntMethod = HuntMethod.fromInt(match.groupValues[4].toInt())!!
                val pokemonEdition = PokemonEdition.fromInt(match.groupValues[5].toInt())!!
                val tabIndex = match.groupValues[6].toInt()
                val internalId = match.groupValues[7].toInt()

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
                repository.insert(pokemonData)
            } else {
                val name = match.groupValues[1]
                val pokedexId = match.groupValues[2].toInt()
                val generation = match.groupValues[3].toInt()
                val encounterNeeded = match.groupValues[4].toInt()
                val huntMethod = HuntMethod.valueOf(match.groupValues[5])
                val pokemonEdition = PokemonEdition.valueOf(match.groupValues[6])
                val tabIndex = match.groupValues[7].toInt()
                val internalId = match.groupValues[8].toInt()

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
                repository.insert(pokemonData)
            }
        }

        return true
    }

    private fun checkCompressed(data: String): Boolean {
        return !data.contains("PokemonData")
    }

}