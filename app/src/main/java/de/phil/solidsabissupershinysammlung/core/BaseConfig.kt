package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

class BaseConfig(context: Context) {

    init {
        prefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)!!
    }

    var firstStart: Boolean
        get() = prefs.getBoolean(FIRST_START, true)
        set(value) = prefs.edit().putBoolean(FIRST_START, value).apply()

    var sortMethod: Int
        get() = prefs.getInt(SORT_METHOD, PokemonSortMethod.InternalId.ordinal)
        set(value) = prefs.edit().putInt(SORT_METHOD, value).apply()

    var isAutoSort: Boolean
        get() = prefs.getBoolean(AUTO_SORT, false)
        set(value) = prefs.edit().putBoolean(AUTO_SORT, value).apply()

    companion object {
        const val PREFERENCE_NAME = "de.phil.solidsabissupershinysammlung_preferences"
        lateinit var prefs: SharedPreferences
        const val FIRST_START = "First Start"
        const val SORT_METHOD = "Sort Method"
        const val AUTO_SORT = "Auto Sort"
    }
}