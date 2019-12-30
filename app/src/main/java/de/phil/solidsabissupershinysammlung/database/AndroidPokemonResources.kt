package de.phil.solidsabissupershinysammlung.database

import de.phil.solidsabissupershinysammlung.ShinyPokemonApplication
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import javax.inject.Inject

class AndroidPokemonResources @Inject constructor() : IAndroidPokemonResources {

    private var gen1Names: Array<String> = ShinyPokemonApplication.applicationContext() .resources.getStringArray(R.array.gen1Names)
    private var gen2Names: Array<String> = ShinyPokemonApplication.applicationContext().resources.getStringArray(R.array.gen2Names)
    private var gen3Names: Array<String> = ShinyPokemonApplication.applicationContext().resources.getStringArray(R.array.gen3Names)
    private var gen4Names: Array<String> = ShinyPokemonApplication.applicationContext().resources.getStringArray(R.array.gen4Names)
    private var gen5Names: Array<String> = ShinyPokemonApplication.applicationContext().resources.getStringArray(R.array.gen5Names)
    private var gen6Names: Array<String> = ShinyPokemonApplication.applicationContext().resources.getStringArray(R.array.gen6Names)
    private var gen7Names: Array<String> = ShinyPokemonApplication.applicationContext().resources.getStringArray(R.array.gen7Names)
    private var gen8Names: Array<String> = ShinyPokemonApplication.applicationContext().resources.getStringArray(R.array.gen8Names)

    private var genNamesArray: Array<Array<String>>

    private var gen1PokedexIds: Array<Int>
    private var gen2PokedexIds: Array<Int>
    private var gen3PokedexIds: Array<Int>
    private var gen4PokedexIds: Array<Int>
    private var gen5PokedexIds: Array<Int>
    private var gen6PokedexIds: Array<Int>
    private var gen7PokedexIds: Array<Int>
    private var gen8PokedexIds: Array<Int>

    private var genPokedexIdsArray: Array<Array<Int>>

    init {

        genNamesArray = arrayOf(
            gen1Names,
            gen2Names,
            gen3Names,
            gen4Names,
            gen5Names,
            gen6Names,
            gen7Names,
            gen8Names
        )

        gen1PokedexIds = ShinyPokemonApplication.applicationContext().resources.getIntArray(R.array.gen1Ids).toTypedArray()
        gen2PokedexIds = ShinyPokemonApplication.applicationContext().resources.getIntArray(R.array.gen2Ids).toTypedArray()
        gen3PokedexIds = ShinyPokemonApplication.applicationContext().resources.getIntArray(R.array.gen3Ids).toTypedArray()
        gen4PokedexIds = ShinyPokemonApplication.applicationContext().resources.getIntArray(R.array.gen4Ids).toTypedArray()
        gen5PokedexIds = ShinyPokemonApplication.applicationContext().resources.getIntArray(R.array.gen5Ids).toTypedArray()
        gen6PokedexIds = ShinyPokemonApplication.applicationContext().resources.getIntArray(R.array.gen6Ids).toTypedArray()
        gen7PokedexIds = ShinyPokemonApplication.applicationContext().resources.getIntArray(R.array.gen7Ids).toTypedArray()
        gen8PokedexIds = ShinyPokemonApplication.applicationContext().resources.getIntArray(R.array.gen8Ids).toTypedArray()

        genPokedexIdsArray = arrayOf(
            gen1PokedexIds,
            gen2PokedexIds,
            gen3PokedexIds,
            gen4PokedexIds,
            gen5PokedexIds,
            gen6PokedexIds,
            gen7PokedexIds,
            gen8PokedexIds
        )
    }

    override fun getPokemonNames(): List<String> {
        return genNamesArray.flatten()
    }

    private fun getPurePokemonName(name: String): String {
        return when {
            name.endsWith(App.ALOLA_EXTENSION) -> name.replace(App.ALOLA_EXTENSION, "")
            name.endsWith(App.GALAR_EXTENSION) -> name.replace(App.GALAR_EXTENSION, "")
            else -> name
        }
    }

    override fun getPokedexIdByName(name: String): Int {

        val nonAlolaName = getPurePokemonName(name)

        var index = 0
        for (array in genNamesArray) {
            for (pokemonName in array) {
                index++

                if (nonAlolaName == pokemonName)
                    return index
            }
        }

        throw Exception()
    }

    override fun getGenerationByName(name: String): Int {

        val nonAlolaName = getPurePokemonName(name)

        var generation = 1
        for (array in genNamesArray) {
            for (pokemonName in array) {

                if (nonAlolaName == pokemonName)
                    return generation
            }
            generation++
        }

        throw Exception()

    }

    override fun getNameByPokedexId(pokedexId: Int): String {

        if (pokedexId !in genPokedexIdsArray.flatten())
            throw Exception("$pokedexId is not a valid pokedex id")

        return getPokemonNames()[pokedexId - 1]
    }

}