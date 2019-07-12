package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import android.util.Log
import de.phil.solidsabissupershinysammlung.fragment.PokemonListChangedListener
import de.phil.solidsabissupershinysammlung.model.IPokemonEngine
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.view.MainView

object App {

    // constants
    const val NUM_TAB_VIEWS = 6
    const val INT_ERROR_CODE = -1
    private const val TAG = "App"

    private var mInitialized = false
    private lateinit var mConfig: BaseConfig

    var config get() = mConfig
    set(value) {
        if (!mInitialized)
            mConfig = value
    }
    lateinit var mainView: MainView
    var dataListDirty = false
    val dataChangedListeners = ArrayList<PokemonListChangedListener>(NUM_TAB_VIEWS)
    lateinit var pokemonEngine: IPokemonEngine

    fun init(context: Context, mainView: MainView) {
        mConfig = BaseConfig(context)
        pokemonEngine = PokemonEngine()

        Log.i(TAG, "initialize pokemon engine")
        pokemonEngine.initialize(context)

        if (config.firstStart)
            config.firstStart = false

        this.mainView = mainView

        mInitialized = true
    }

    fun finish() {
        pokemonEngine.finish()
    }

    fun setSortMethod(sortMethod: PokemonSortMethod) {
        mConfig.sortMethod = sortMethod.ordinal
    }

    fun getSortMethod(): PokemonSortMethod {
        return PokemonSortMethod.fromInt(mConfig.sortMethod)!!
    }

    fun performAutoSort() {
        if (mConfig.isAutoSort && dataChangedListeners.size == NUM_TAB_VIEWS) {
            for (i in 0 until NUM_TAB_VIEWS)
                dataChangedListeners[i].notifySortPokemon(getSortMethod())
        }
    }
}