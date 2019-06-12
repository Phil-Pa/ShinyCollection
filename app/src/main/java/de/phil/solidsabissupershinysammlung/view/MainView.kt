package de.phil.solidsabissupershinysammlung.view

import de.phil.solidsabissupershinysammlung.model.PokemonData

interface MainView {

    fun showMessage(message: String)

    // navigation drawer methods
    fun startAddNewPokemonActivity()

    // call showPokemonDetailDialog
    fun onListEntryClick(data: PokemonData?)

    // show modal bottom sheet
    fun onListEntryLongClick(data: PokemonData?)

    fun updateShinyStatistics()
}