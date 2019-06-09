package de.phil.solidsabissupershinysammlung.presenter

import android.view.View
import de.phil.solidsabissupershinysammlung.model.PokemonData

interface AddNewPokemonViewPresenter {

    fun addPokemonToDatabase(data: PokemonData)
    fun addPokemonButtonClick(view: View?)

}