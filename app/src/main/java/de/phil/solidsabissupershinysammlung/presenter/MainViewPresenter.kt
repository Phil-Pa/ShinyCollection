package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.model.PokemonData

interface MainViewPresenter {

    fun getAverageEggsCount(): Float
    fun getTotalEggsCount(): Int
    fun getTotalNumberOfShinys(): Int

    fun deletePokemonFromDatabase(data: PokemonData)
    fun exportData(): String

}