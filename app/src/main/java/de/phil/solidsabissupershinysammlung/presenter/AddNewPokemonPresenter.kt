package de.phil.solidsabissupershinysammlung.presenter

import android.view.View
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.AddNewPokemonView

class AddNewPokemonPresenter(private val addNewPokemonView: AddNewPokemonView) : AddNewPokemonViewPresenter {
    override fun addPokemonButtonClick(view: View?) {
        val position = addNewPokemonView.getSelectedSpinnerPosition()

        val huntMethod = HuntMethod.fromInt(position)
        val name = addNewPokemonView.getPokemonName()

        if (name.isEmpty() || name.isBlank()) {
            addNewPokemonView.showMessage("Du musst einen Namen für das Pokemon eingeben!")
            return
        }

        val names = App.getAllPokemonNames()
        val alolaNames = App.getAllPokemonAlolaNames()

        if (!names.contains(name) && !alolaNames.contains(name))
            addNewPokemonView.showMessage("Es gibt kein Pokemon namens $name!")

        val encounters = addNewPokemonView.getEncounters()
        if (encounters == -1) {
            addNewPokemonView.showMessage("Du musst angeben, wie viele Encounter du gebraucht hast!")
            return
        }

        val pair = addNewPokemonView.getPokedexIdAndGeneration(name)
        val tabIndex = addNewPokemonView.getPokemonListTabIndex()

        val data = PokemonData(name, pair.first, pair.second, encounters, huntMethod!!)

        // TODO if tab index == 0, add pokemon data to normal table, else add to other table containing the pokemon lists

        if (tabIndex == 0) {
            App.addPokemonToShinyList(data)
        }

        addNewPokemonView.clearUserInput()
        addNewPokemonView.returnToMainActivity()
    }

}