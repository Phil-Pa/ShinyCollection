package de.phil.solidsabissupershinysammlung.database

import android.content.Context
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.ShinyPokemonApplication

class AndroidPokemonResources(context: Context) : IAndroidPokemonResources {

    private var gen1Names: Array<String> = context.resources.getStringArray(R.array.gen1Names)
    private var gen2Names: Array<String> = context.resources.getStringArray(R.array.gen2Names)
    private var gen3Names: Array<String> = context.resources.getStringArray(R.array.gen3Names)
    private var gen4Names: Array<String> = context.resources.getStringArray(R.array.gen4Names)
    private var gen5Names: Array<String> = context.resources.getStringArray(R.array.gen5Names)
    private var gen6Names: Array<String> = context.resources.getStringArray(R.array.gen6Names)
    private var gen7Names: Array<String> = context.resources.getStringArray(R.array.gen7Names)
    private var gen8Names: Array<String> = context.resources.getStringArray(R.array.gen8Names)

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

        gen1PokedexIds = context.resources.getIntArray(R.array.gen1Ids).toTypedArray()
        gen2PokedexIds = context.resources.getIntArray(R.array.gen2Ids).toTypedArray()
        gen3PokedexIds = context.resources.getIntArray(R.array.gen3Ids).toTypedArray()
        gen4PokedexIds = context.resources.getIntArray(R.array.gen4Ids).toTypedArray()
        gen5PokedexIds = context.resources.getIntArray(R.array.gen5Ids).toTypedArray()
        gen6PokedexIds = context.resources.getIntArray(R.array.gen6Ids).toTypedArray()
        gen7PokedexIds = context.resources.getIntArray(R.array.gen7Ids).toTypedArray()
        gen8PokedexIds = context.resources.getIntArray(R.array.gen8Ids).toTypedArray()

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
            name.endsWith(ShinyPokemonApplication.ALOLA_EXTENSION) -> name.replace(
                ShinyPokemonApplication.ALOLA_EXTENSION, "")
            name.endsWith(ShinyPokemonApplication.GALAR_EXTENSION) -> name.replace(
                ShinyPokemonApplication.GALAR_EXTENSION, "")
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