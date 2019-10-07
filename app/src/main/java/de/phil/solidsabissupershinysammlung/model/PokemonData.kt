package de.phil.solidsabissupershinysammlung.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.phil.solidsabissupershinysammlung.core.App

@Entity
data class PokemonData(
    @PrimaryKey(autoGenerate = true)
    val internalId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "pokedex_id")
    val pokedexId: Int,

    @ColumnInfo(name = "generation")
    val generation: Int,

    @ColumnInfo(name = "encounter_needed")
    val encounterNeeded: Int,

    @ColumnInfo(name = "hunt_method")
    val huntMethod: HuntMethod,

    @ColumnInfo(name = "tab_index")
    val tabIndex: Int

//    @ColumnInfo
//    val isAlola: Boolean
) {

    // TODO: remove
    private fun isAlola() = App.pokemonEngine.getAllPokemonAlolaNames().contains(name)

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