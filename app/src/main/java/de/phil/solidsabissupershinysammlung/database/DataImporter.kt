package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.ShinyPokemonApplication
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition

class DataImporter {

    companion object {
        private const val defaultRegex =
            "PokemonData\\(name=([\\w+\\-\\d:]+), pokedexId=(\\d+), generation=(\\d), encounterNeeded=(\\d+), huntMethod=(\\w+), pokemonEdition=(\\w+), tabIndex=(\\d), internalId=(\\d+)\\)"
    }

    fun import(pokemonDao: PokemonDao, data: String?): Boolean {
        if (data == null)
            return false

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

}