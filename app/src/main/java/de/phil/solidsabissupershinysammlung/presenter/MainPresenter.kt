package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.MainView

class MainPresenter(private val mainView: MainView) : MainViewPresenter {
    override fun getAverageEggsCount(): Float {
        return App.getAverageEggsCount().toFloat()
    }

    override fun getTotalEggsCount(): Int {
        return App.getTotalEggsCount()
    }

    override fun getTotalNumberOfShinys(): Int {
        return App.getTotalNumberOfShinys()
    }

    override fun deletePokemonFromDatabase(data: PokemonData) {
        App.deletePokemonFromDatabase(data, mainView.getCurrentTabIndex())
    }

}