package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import android.util.Log
import de.phil.solidsabissupershinysammlung.fragment.PokemonListChangedListener
import de.phil.solidsabissupershinysammlung.model.IPokemonEngine
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

object App {

    // constants
    const val NUM_TAB_VIEWS = 6
    const val INT_ERROR_CODE = -1

    private var mInitialized = false
    private lateinit var mConfig: BaseConfig

    var dataListDirty = false
    val dataChangedListeners = ArrayList<PokemonListChangedListener>(NUM_TAB_VIEWS)
    lateinit var pokemonEngine: IPokemonEngine

    fun setSortMethod(sortMethod: PokemonSortMethod) {
        mConfig.sortMethod = sortMethod.ordinal
    }

    fun getSortMethod(): PokemonSortMethod {
        // TODO: use room database?
//        return PokemonSortMethod.fromInt(mConfig.sortMethod)!!
        return PokemonSortMethod.Name
    }

    fun performAutoSort() {
//        if (mConfig.isAutoSort && dataChangedListeners.size == NUM_TAB_VIEWS) {
//            for (i in 0 until NUM_TAB_VIEWS)
//                dataChangedListeners[i].notifySortPokemon(getSortMethod())
//        }
    }

    const val REQUEST_ADD_POKEMON = 1

    const val PREFERENCES_NAME = "MyPreferences"
    const val PREFERENCES_GUIDE_SHOWN = "guide_shown"
    const val PREFERENCES_SORT_METHOD = "sort_method"
    const val PREFERENCES_AUTO_SORT = "auto_sort"

}