package de.phil.solidsabissupershinysammlung.presenter

import android.view.View
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.AddNewPokemonView

class AddNewPokemonPresenter(private val addNewPokemonView: AddNewPokemonView) : AddNewPokemonViewPresenter {
    override fun addPokemonButtonClick(view: View?) {

        val tabIndex = addNewPokemonView.getPokemonListTabIndex()
        val huntMethod = if (tabIndex == 0) HuntMethod.fromInt(addNewPokemonView.getSelectedSpinnerPosition()) else HuntMethod.Other
        val name = addNewPokemonView.getPokemonName()

        if (name.isEmpty() || name.isBlank()) {
            addNewPokemonView.showMessage("Du musst einen Namen für das Pokemon eingeben!")
            return
        }

        val names = App.getAllPokemonNames()
        val alolaNames = App.getAllPokemonAlolaNames()

        if (!names.contains(name) && !alolaNames.contains(name)) {
            addNewPokemonView.showMessage("Es gibt kein Pokemon namens $name!")
            return
        }

        val encounters = addNewPokemonView.getEncounters()
        if (encounters == App.INT_ERROR_CODE && tabIndex == 0) {
            addNewPokemonView.showMessage("Du musst angeben, wie viele Encounter du gebraucht hast!")
            return
        }

        val pair = addNewPokemonView.getPokedexIdAndGeneration(name)
        if (pair == null) {
            // TODO implement app logger
            addNewPokemonView.showMessage("Von $name konnte die PokedexID und Generation nicht bestimmt werden.")
            return
        }
        val (pokedexId, generation) = pair


        val data = PokemonData(name, pokedexId, generation, encounters, huntMethod!!)

        App.addPokemon(data, tabIndex)

        addNewPokemonView.clearUserInput()
        addNewPokemonView.returnToMainActivity()
    }

}