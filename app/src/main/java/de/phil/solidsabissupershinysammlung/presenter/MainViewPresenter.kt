package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.model.PokemonData

interface MainViewPresenter {

    fun deletePokemonFromDatabase(data: PokemonData)
    fun exportData()
    fun importData()
    fun setNavigationViewData()
    fun showRandomPokemon()
    fun startAddNewPokemonActivity()
    fun sortData()

}