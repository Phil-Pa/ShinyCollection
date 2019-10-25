package de.phil.solidsabissupershinysammlung.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(application.packageName + App.PREFERENCES_NAME, Context.MODE_PRIVATE)

        initPreferences()
    }

    private fun initPreferences() {

        with (preferences) {

            if (!contains(App.PREFERENCES_AUTO_SORT))
                edit().putBoolean(App.PREFERENCES_AUTO_SORT, false).apply()

            if (!contains(App.PREFERENCES_SORT_METHOD))
                edit().putInt(App.PREFERENCES_SORT_METHOD, PokemonSortMethod.InternalId.ordinal).apply()

            if (!contains(App.PREFERENCES_GUIDE_SHOWN))
                edit().putBoolean(App.PREFERENCES_GUIDE_SHOWN, false).apply()
        }

    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}