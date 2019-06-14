package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import de.phil.solidsabissupershinysammlung.view.MainView

class MainPresenter(private val mainView: MainView) : MainViewPresenter {
    override fun getAverageEggsCount(): Float {
        return PokemonEngine.getAverageEggsCount().toFloat()
    }

    override fun getTotalEggsCount(): Int {
        return PokemonEngine.getTotalEggsCount()
    }

    override fun getTotalNumberOfShinys(): Int {
        return PokemonEngine.getTotalNumberOfShinys()
    }

    override fun deletePokemonFromDatabase(data: PokemonData) {
        PokemonEngine.deletePokemonFromDatabase(data, mainView.getCurrentTabIndex())
    }

}