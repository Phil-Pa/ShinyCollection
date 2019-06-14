package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val FIRST_START = "First Start"

class BaseConfig(context: Context) {

    private val prefs = context.getSharedPreferences("Preferences", MODE_PRIVATE)!!

    var firstStart: Boolean
        get() = prefs.getBoolean(FIRST_START, true)
        set(value) = prefs.edit().putBoolean(FIRST_START, value).apply()

}