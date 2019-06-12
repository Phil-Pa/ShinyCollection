package de.phil.solidsabissupershinysammlung.presenter

import android.view.View
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.MainView

class MainPresenter(private val mainView: MainView) : MainViewPresenter {

    override fun getAverageEggEncounters(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllEggEncounters(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMessage(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deletePokemonFromDatabase(data: PokemonData, tabIndex: Int) {
        App.deletePokemonFromDatabase(data, tabIndex)
    }

}