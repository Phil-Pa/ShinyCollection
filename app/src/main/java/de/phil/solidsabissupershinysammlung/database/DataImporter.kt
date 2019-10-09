package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData

class DataImporter {

    fun import(repository: PokemonRepository, data: String?): Boolean {
        if (data == null)
            return false

        val dataList = data.split("\n")
        val regex = Regex("PokemonData\\(name=([\\w+\\-\\d:]+), pokedexId=(\\d+), generation=(\\d), encounterNeeded=(\\d+), huntMethod=(\\w+), tabIndex=(\\d), internalId=(\\d+)\\)")

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

            repository.insert(
                PokemonData(
                    internalId,
                    name,
                    pokedexId,
                    generation,
                    encounterNeeded,
                    huntMethod,
                    tabIndex
                )
            )
        }
        return true
    }

}