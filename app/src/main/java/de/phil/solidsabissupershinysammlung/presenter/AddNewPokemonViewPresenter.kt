package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.model.PokemonData

interface AddNewPokemonViewPresenter {

    fun showMessage(message: String)

    fun addPokemonToDatabase(data: PokemonData)

    fun getSelectedSpinnerPosition() : Int

    fun returnToMainActivity()

}