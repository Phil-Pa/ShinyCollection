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

    private fun isAlola(): Boolean {
        return name in alolaPokemon
    }

    private fun isGalar(): Boolean {
        return name in galarPokemon
    }

    fun getDownloadUrl(): String {

        val baseString = when {
            generation == 8 || isGalar() -> "https://media.bisafans.de/67fac06/pokemon/gen8/swsh/shiny/"
            else -> "https://media.bisafans.de/d4c7a05/pokemon/gen7/sm/shiny/"
        }

        return "$baseString${getBitmapFileName()}"
    }

    private fun getBitmapFileName(): String {
        val generationString = StringBuilder(pokedexId.toString())
        while (generationString.length < 3)
            generationString.insert(0, '0')

        when {
            isAlola() -> generationString.append(ShinyPokemonApplication.ALOLA_EXTENSION)
            isGalar() -> generationString.append(ShinyPokemonApplication.GALAR_EXTENSION)
        }

        return "$generationString.png"
    }

    override fun toString(): String {
        return "PokemonData(name=$name, pokedexId=$pokedexId, generation=$generation, encounterNeeded=$encounterNeeded, huntMethod=$huntMethod, pokemonEdition=$pokemonEdition, tabIndex=$tabIndex, internalId=$internalId)"
    }

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
            isAlola() -> '1'
            isGalar() -> '2'
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

        private val alolaPokemon = listOf(
            // de
            "Rattfratz", "Rattikarl", "Raichu", "Sandan", "Sandamer",
            "Vulpix", "Vulnona", "Digda", "Digdri", "Mauzi", "Snobilikat", "Kleinstein",
            "Georok", "Geowaz", "Sleima", "Sleimok", "Kokowei", "Knogga",

            // en
            // raichu, vulpix are de and en
            "Rattata", "Raticate", "Sandshrew", "Sandslash",
            "Ninetales", "Diglett", "Dugtrio", "Meowth", "Persian", "Geodude",
            "Graveler", "Golem", "Grimer", "Muk", "Exeggutor", "Marowak"

        ).map { "$it${ShinyPokemonApplication.ALOLA_EXTENSION}" }

        private val galarPokemon = listOf(
            // de
            "Mauzi", "Flegmon", "Lahmus", "Ponita", "Gallopa", "Porenta", "Smogmog", "Pantimos",
            "Corasonn", "Zigzachs", "Geradaks", "Flampion", "Flampivian",
            "Makabaja", "Flunschlik",

            // en
            "Meowth", "Slowpoke", "Slowbro", "Ponyta", "Rapidash", "Farfetch’d", "Weezing", "Mr. Mime",
            "Corsola", "Zigzagoon", "Linoone", "Darumaka", "Darmanitan",
            "Yamask", "Yamask"
        ).map { "$it${ShinyPokemonApplication.GALAR_EXTENSION}" }

    }

}