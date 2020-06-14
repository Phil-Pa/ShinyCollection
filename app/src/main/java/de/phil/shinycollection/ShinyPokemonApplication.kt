package de.phil.shinycollection

import android.app.Application

class ShinyPokemonApplication : Application() {

    companion object {
        // constants
        const val NUM_TAB_VIEWS = 5
        const val ENCOUNTER_UNKNOWN = 0
        const val TAB_INDEX_SHINY_LIST = 0

        const val PREFERENCES_NAME = "_preferences"
        const val PREFERENCES_GUIDE_SHOWN = "guide_shown"
        const val PREFERENCES_SORT_METHOD = "sort_method"
        const val PREFERENCES_AUTO_SORT = "auto_sort"
        const val PREFERENCES_USE_DARK_MODE = "use_dark_mode"
        const val PREFERENCES_POKEMON_EDITION = "pokemon_edition"
        const val PREFERENCES_CURRENT_THEME = "current_theme"

        const val ALOLA_EXTENSION = "-alola"
        const val GALAR_EXTENSION = "-galar"
    }
}