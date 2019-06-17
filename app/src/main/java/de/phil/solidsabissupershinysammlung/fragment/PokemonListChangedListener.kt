package de.phil.solidsabissupershinysammlung.fragment

import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

interface PokemonListChangedListener {

    fun notifyPokemonAdded(data: PokemonData)
    fun notifyPokemonDeleted(tabIndex: Int, position: Int)
    fun notifyAllPokemonDeleted(tabIndex: Int)
    fun notifySortPokemon(sortMethod: PokemonSortMethod)

}