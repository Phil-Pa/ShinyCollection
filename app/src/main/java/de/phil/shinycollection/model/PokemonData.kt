package de.phil.shinycollection.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.phil.shinycollection.ShinyPokemonApplication

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
        return "($pokedexId, $generation, $encounterNeeded, ${huntMethod.ordinal}, ${pokemonEdition.ordinal}, $tabIndex, $internalId)"
    }

    companion object {

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