package de.phil.solidsabissupershinysammlung.view

interface AddNewPokemonView {

    fun showMessage(message: String)

    fun getSelectedSpinnerPosition() : Int

    fun returnToMainActivity()

    fun getPokemonName(): String

    fun getEncounters(): Int

    fun clearUserInput()

    fun getPokedexIdAndGeneration(_name: String): Pair<Int, Int>?

    fun getPokemonListTabIndex(): Int

}