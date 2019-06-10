package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import android.content.Context.MODE_PRIVATE
import java.lang.IllegalStateException

private const val APP_NAME = "SolidSabisShinySammlung"
private const val FIRST_START = "First Start"
private const val NUMBER_OF_POKEMON_LISTS = "Number of Pokemon Lists"

class BaseConfig(context: Context) {

    private val prefs = context.getSharedPreferences("Preferences", MODE_PRIVATE)!!

    var appName: String
        get() = prefs.getString(APP_NAME, "")!!
        set(value) = prefs.edit().putString(APP_NAME, value).apply()

    var firstStart: Boolean
        get() = prefs.getBoolean(FIRST_START, true)
        set(value) = prefs.edit().putBoolean(FIRST_START, value).apply()

    var numberOfPokemonLists: Int
        get() = prefs.getInt(NUMBER_OF_POKEMON_LISTS, 0)
        set(value) = prefs.edit().putInt(NUMBER_OF_POKEMON_LISTS, value).apply()

}