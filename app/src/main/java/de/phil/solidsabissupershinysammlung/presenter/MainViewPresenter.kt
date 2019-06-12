package de.phil.solidsabissupershinysammlung.presenter

import android.view.View
import de.phil.solidsabissupershinysammlung.model.PokemonData

interface MainViewPresenter {

    fun showMessage(message: String)

    // TODO show the average egg count, the entire egg count and the number of all pokemon in the top of the navigation drawer and
    fun getAverageEggEncounters(): Float
    fun getAllEggEncounters(): Int

    // modal bottom sheet methods
    fun deletePokemonFromDatabase(data: PokemonData, tabIndex: Int)
    // fun addPokemonToTab(tabIndex: Int)
    // fun removePokemonFromTab(tabIndex: Int)

}