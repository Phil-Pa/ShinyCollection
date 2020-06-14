package de.phil.shinycollection.activity

import android.content.Context
import android.view.View
import de.phil.shinycollection.model.PokemonData
import de.phil.shinycollection.model.PokemonSortMethod

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