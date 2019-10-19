package de.phil.solidsabissupershinysammlung.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "pokedex_id")
    val pokedexId: Int,

    @ColumnInfo(name = "generation")
    val generation: Int,

    @ColumnInfo(name = "encounter_needed")
    val encounterNeeded: Int,

    @ColumnInfo(name = "hunt_method")
    val huntMethod: HuntMethod = HuntMethod.Other,

    @ColumnInfo(name = "tab_index")
    val tabIndex: Int
) {

    @PrimaryKey(autoGenerate = true)
    var internalId: Int = 1

    // TODO
    private fun isAlola() = false//App.pokemonEngine.getAllPokemonAlolaNames().contains(name)

    fun getDownloadUrl(): String {
        val baseString = "https://media.bisafans.de/d4c7a05/pokemon/gen7/sm/shiny/"


        return "$baseString${getBitmapFileName()}"
    }

    fun getBitmapFileName(): String {
        val generationString = StringBuilder(pokedexId.toString())
        while (generationString.length < 3)
            generationString.insert(0, '0')

        if (isAlola()) {
            generationString.append("-alola")
        }
        return "$generationString.png"
    }

}