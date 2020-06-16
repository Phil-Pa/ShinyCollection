package de.phil.shinycollection.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.phil.shinycollection.BuildConfig
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.utils.prependZerosToNumber

class UpdateStatisticsData(
    val totalNumberOfShiny: Int,
    val totalNumberOfEggShiny: Int,
    val totalNumberOfSosShiny: Int,
    val averageSos: Float,
    val totalEggs: Int,
    val averageEggs: Float
)

const val INTENT_EXTRA_HUNT_METHOD = "hunt_method"
const val INTENT_EXTRA_NAME = "name"
const val INTENT_EXTRA_ENCOUNTERS = "encounter_needed"
const val INTENT_EXTRA_POKEDEX_ID = "pokedex_id"
const val INTENT_EXTRA_GENERATION = "generation"
const val INTENT_EXTRA_TAB_INDEX = "tab_index"
const val INTENT_EXTRA_POKEMON_EDITION = "pokemon_edition"

@Entity
data class PokemonData(

    @ColumnInfo(name = INTENT_EXTRA_NAME)
    val name: String,

    @ColumnInfo(name = INTENT_EXTRA_POKEDEX_ID)
    var pokedexId: Int,

    @ColumnInfo(name = INTENT_EXTRA_GENERATION)
    var generation: Int,

    @ColumnInfo(name = INTENT_EXTRA_ENCOUNTERS)
    var encounterNeeded: Int,

    @ColumnInfo(name = INTENT_EXTRA_HUNT_METHOD)
    var huntMethod: HuntMethod = HuntMethod.Other,

    @ColumnInfo(name = INTENT_EXTRA_POKEMON_EDITION)
    var pokemonEdition: PokemonEdition,

    @ColumnInfo(name = INTENT_EXTRA_TAB_INDEX)
    var tabIndex: Int
) {

    @PrimaryKey(autoGenerate = true)
    var internalId: Int = 1

    fun getDownloadUrl() = Companion.getDownloadUrl(generation, pokedexId)

    override fun toString() = "PokemonData(name=$name, pokedexId=$pokedexId, generation=$generation, encounterNeeded=$encounterNeeded, huntMethod=$huntMethod, pokemonEdition=$pokemonEdition, tabIndex=$tabIndex, internalId=$internalId)"

    fun toShortString(): String {

        // 0 -> 3 bytes -> pokedexId
        // 1 -> 1 byte -> generation
        // 2 -> 5 byte -> encounters
        // 3 -> 2 byte -> huntMethod
        // 4 -> 1 byte -> pokemonEdition
        // 5 -> 1 byte -> tabIndex
        // 6 -> 1 byte -> pokemon alternative form | 0 = normal form, 1 = alola, 2 = galar
        // 7 -> 3 byte -> internalId

        val sb = StringBuilder(SHORT_DATA_STRING_LENGTH)

        val id = prependZerosToNumber(3, pokedexId)
        val gen = generation.toString()
        val encounters = prependZerosToNumber(5, encounterNeeded)
        val huntMethod = prependZerosToNumber(2, huntMethod.ordinal)
        val pokemonEdition = pokemonEdition.ordinal.toString()
        val tabIndex = tabIndex.toString()

        val pokemonAlternativeFormFlag = when {
            isAlola(pokedexId) -> '1'
            isGalar(pokedexId) -> '2'
            else -> '0'
        }

        val internalId = prependZerosToNumber(3, internalId)

        sb.append(id)
        sb.append(gen)
        sb.append(encounters)
        sb.append(huntMethod)
        sb.append(pokemonEdition)
        sb.append(tabIndex)
        sb.append(pokemonAlternativeFormFlag)
        sb.append(internalId)

        val shortString = sb.toString()

        if (BuildConfig.DEBUG && shortString.length != SHORT_DATA_STRING_LENGTH) {
            error("Assertion failed")
        }

        return shortString
    }

    companion object {

        const val SHORT_DATA_STRING_LENGTH = 17

        private val alolaPokemonIds = arrayOf(19, 20, 26, 27, 28, 37, 38, 50, 51, 52, 53, 74, 75, 76, 88, 89, 103, 105)

        private val galarPokemonIds = arrayOf(52, 77, 78, 79, 80, 83, 110, 122, 144, 145, 146, 199, 222, 263, 264, 554, 555, 562, 618)

        fun isAlola(pokedexId: Int) = pokedexId in alolaPokemonIds

        fun isGalar(pokedexId: Int) = pokedexId in galarPokemonIds

        private fun getBitmapFileName(pokedexId: Int): String {

            val generationString = prependZerosToNumber(3, pokedexId)
            val sb = StringBuilder(generationString.length + ShinyPokemonApplication.ALOLA_EXTENSION.length)
            sb.append(generationString)

            when {
                isAlola(pokedexId) -> sb.append(ShinyPokemonApplication.ALOLA_EXTENSION)
                isGalar(pokedexId) -> sb.append(ShinyPokemonApplication.GALAR_EXTENSION)
            }

            return "$generationString.png"
        }

        fun getDownloadUrl(generation: Int, pokedexId: Int): String {

            val baseString = when {
                generation == 8 || isGalar(pokedexId) -> "https://media.bisafans.de/67fac06/pokemon/gen8/swsh/shiny/"
                else -> "https://media.bisafans.de/d4c7a05/pokemon/gen7/sm/shiny/"
            }

            return "$baseString${getBitmapFileName(pokedexId)}"
        }

    }

}