package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import android.content.Context.MODE_PRIVATE
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

private const val FIRST_START = "First Start"
private const val SORT_METHOD = "Sort Method"

class BaseConfig(context: Context) {

    private val prefs = context.getSharedPreferences("Preferences", MODE_PRIVATE)!!

    var firstStart: Boolean
        get() = prefs.getBoolean(FIRST_START, true)
        set(value) = prefs.edit().putBoolean(FIRST_START, value).apply()

    var sortMethod: Int
        get() = prefs.getInt(SORT_METHOD, PokemonSortMethod.InternalId.ordinal)
        set(value) = prefs.edit().putInt(SORT_METHOD, value).apply()
}