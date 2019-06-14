package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import android.util.Log
import de.phil.solidsabissupershinysammlung.fragment.PokemonListChangedListener
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import de.phil.solidsabissupershinysammlung.view.MainView

object App {

    const val NUM_TAB_VIEWS = 4
    const val INT_ERROR_CODE = -1

    private var mInitialized = false
    private lateinit var mConfig: BaseConfig

    var config get() = mConfig
    set(value) {
        if (!mInitialized)
            mConfig = value
    }

    private var mMainView: MainView? = null

    var mainView get() = mMainView
    set(value) {
        if (mMainView == null)
            mMainView = value
    }

    val dataChangedListeners = ArrayList<PokemonListChangedListener>(NUM_TAB_VIEWS)

    private const val TAG = "App"

    fun init(context: Context) {
        mConfig = BaseConfig(context)

        Log.i(TAG, "initialize pokemon engine")
        PokemonEngine.initialize(context)

        if (config.firstStart)
            config.firstStart = false
        mInitialized = true
    }

    fun updateShinyStatistics() {
        Log.i(TAG, "update shiny statistics")
        mainView?.updateShinyStatistics()
    }

    fun finish() {
        PokemonEngine.finish()
    }

}