package de.phil.solidsabissupershinysammlung.activity

import android.content.Context
import android.view.View
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

interface IPokemonListActivity {
    fun addRecyclerViewChangedListener(listener: MainActivity.OnListChangedListener)
    fun getAllPokemonDataFromTabIndex(mTabIndex: Int): List<PokemonData>
    fun getSortMethod(): PokemonSortMethod
    fun getContext(): Context
    // view has tag with pokemon data filled
    fun onListEntryLongClick(view: View)
    fun onListEntryClick(pokemonData: PokemonData)
    fun showSmallIcons(): Boolean
}