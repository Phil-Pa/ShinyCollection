package de.phil.solidsabissupershinysammlung.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.phil.solidsabissupershinysammlung.activity.AddNewPokemonActivity
import de.phil.solidsabissupershinysammlung.core.App

class UpdateStatisticsData(
    val totalNumberOfShiny: Int,
    val totalNumberOfEggShiny: Int,
    val totalNumberOfSosShiny: Int,
    val averageSos: Float,
    val totalEggs: Int,
    val averageEggs: Float
)

@Entity
data class PokemonData(

    @ColumnInfo(name = AddNewPokemonActivity.INTENT_EXTRA_NAME)
    val name: String,

    @ColumnInfo(name = AddNewPokemonActivity.INTENT_EXTRA_POKEDEX_ID)
    val pokedexId: Int,

    @ColumnInfo(name = AddNewPokemonActivity.INTENT_EXTRA_GENERATION)
    val generation: Int,

    @ColumnInfo(name = AddNewPokemonActivity.INTENT_EXTRA_ENCOUNTERS)
    var encounterNeeded: Int,

    @ColumnInfo(name = AddNewPokemonActivity.INTENT_EXTRA_HUNT_METHOD)
    val huntMethod: HuntMethod = HuntMethod.Other,

    @ColumnInfo(name = AddNewPokemonActivity.INTENT_EXTRA_POKEMON_EDITION)
    val pokemonEdition: PokemonEdition,

    @ColumnInfo(name = AddNewPokemonActivity.INTENT_EXTRA_TAB_INDEX)
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
            isAlola() -> generationString.append(App.ALOLA_EXTENSION)
            isGalar() -> generationString.append(App.GALAR_EXTENSION)
        }

        return "$generationString.png"
    }

    override fun toString(): String {
        return "PokemonData(name=$name, pokedexId=$pokedexId, generation=$generation, encounterNeeded=$encounterNeeded, huntMethod=$huntMethod, pokemonEdition=$pokemonEdition, tabIndex=$tabIndex, internalId=$internalId)"
    }

    fun toShortString(): String {
        return "($name, $pokedexId, $generation, $encounterNeeded, $huntMethod, $pokemonEdition, $tabIndex, $internalId)"
    }

    companion object {

        private val alolaPokemon = listOf(
            "Rattfratz", "Rattikarl", "Raichu", "Sandan", "Sandamer",
            "Vulpix", "Vulnona", "Digda", "Digdri", "Mauzi", "Snobilikat", "Kleinstein",
            "Georok", "Geowaz", "Sleima", "Sleimok", "Kokowei", "Knogga"
        ).map { "$it${App.ALOLA_EXTENSION}" }

        private val galarPokemon = listOf(
            "Mauzi", "Ponita", "Gallopa", "Porenta", "Smogmog", "Pantimos",
            "Corasonn", "Zigzachs", "Geradaks", "Flampion", "Flampivian",
            "Makabaja", "Flunschlik"
        ).map { "$it${App.GALAR_EXTENSION}" }

    }

}