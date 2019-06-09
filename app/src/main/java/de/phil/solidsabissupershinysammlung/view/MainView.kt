package de.phil.solidsabissupershinysammlung.view

import de.phil.solidsabissupershinysammlung.model.PokemonData

interface MainView {

    fun showMessage(message: String)

    // navigation drawer methods
    fun startAddNewPokemonActivity()
    fun addPokemonList()
    // get the list index using a message dialog
    fun removePokemonList()
    fun showAverageEggEncounters()
    fun showAllEggEncounters()

    // TODO create search method for filtering the list, depending on the tab

    // shows a dialog with detailed information about the pokemon if the user
    // holds shortly the list entry -> using onClick event?
    fun showPokemonDetailDialog(data: PokemonData)

    // tab handling
    fun getCurrentTabIndex(): Int

    // call showPokemonDetailDialog
    fun onListEntryClick(data: PokemonData?)

    // show modal bottom sheet
    fun onListEntryLongClick(data: PokemonData?)
}