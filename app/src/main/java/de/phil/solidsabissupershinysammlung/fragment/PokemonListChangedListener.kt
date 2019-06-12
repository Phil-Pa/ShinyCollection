package de.phil.solidsabissupershinysammlung.fragment

import de.phil.solidsabissupershinysammlung.model.PokemonData

interface PokemonListChangedListener {

    fun notifyPokemonAdded(data: PokemonData, tabIndex: Int)
    fun notifyPokemonDeleted(tabIndex: Int, position: Int)

}