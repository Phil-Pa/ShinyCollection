package de.phil.solidsabissupershinysammlung.activity

import android.content.Context
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

interface IPokemonListActivity {
    fun addRecyclerViewChangedListener(listener: MainActivity.OnListChangedListener)
    fun getAllPokemonDataFromTabIndex(mTabIndex: Int): List<PokemonData>
    fun getSortMethod(): PokemonSortMethod
    fun getContext(): Context
    fun onListEntryLongClick(pokemonData: PokemonData)
    fun onListEntryClick(pokemonData: PokemonData)
    fun showSmallIcons(): Boolean
}