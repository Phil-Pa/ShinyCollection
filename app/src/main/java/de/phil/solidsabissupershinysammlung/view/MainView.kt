package de.phil.solidsabissupershinysammlung.view

import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

interface MainView {

    fun showMessage(message: String)

    // navigation drawer methods
    fun startAddNewPokemonActivity(tabIndex: Int)

    // call showPokemonDetailDialog
    fun onListEntryClick(data: PokemonData)

    // show modal bottom sheet
    fun onListEntryLongClick(data: PokemonData)

    fun updateShinyStatistics(numberOfShinys: Int, numberOfEggShinys: Int, numberOfSosShinys: Int, totalEggsCount: Int, averageEggsCount: Float)

    fun getCurrentTabIndex(): Int
    fun copyToClipboard(data: String)
    fun getClipboardStringData(): String?

    fun showDialog(action: (PokemonSortMethod) -> Unit)
}