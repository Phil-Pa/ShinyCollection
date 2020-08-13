package de.phil.shinycollection.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import de.phil.shinycollection.R
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.model.PokemonSortMethod

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTheme()
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(application.packageName + ShinyPokemonApplication.PREFERENCES_NAME, Context.MODE_PRIVATE)

        initPreferences()
        initDarkMode()
    }

    private fun initDarkMode() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (prefs.getBoolean(ShinyPokemonApplication.PREFERENCES_USE_DARK_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun initPreferences() {

        with (preferences) {

            if (!contains(ShinyPokemonApplication.PREFERENCES_AUTO_SORT))
                edit().putBoolean(ShinyPokemonApplication.PREFERENCES_AUTO_SORT, false).apply()

            if (!contains(ShinyPokemonApplication.PREFERENCES_SORT_METHOD))
                edit().putInt(ShinyPokemonApplication.PREFERENCES_SORT_METHOD, PokemonSortMethod.InternalId.ordinal).apply()

            if (!contains(ShinyPokemonApplication.PREFERENCES_GUIDE_SHOWN))
                edit().putBoolean(ShinyPokemonApplication.PREFERENCES_GUIDE_SHOWN, false).apply()

            if (!contains(ShinyPokemonApplication.PREFERENCES_CURRENT_THEME)) {
                edit().putString(ShinyPokemonApplication.PREFERENCES_CURRENT_THEME, getString(R.string.theme_orange)).apply()
            }
        }


    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val themePref = this.findPreference<ListPreference>("current_theme")!!

            findPreference<PreferenceScreen>("app_version")?.summary = context?.packageManager?.getPackageInfo(context?.packageName!!, 0)?.versionName

            themePref.setOnPreferenceChangeListener { _, newValue ->
                val newTheme = newValue as String
                initTheme(newTheme)

                activity?.recreate()

                true
            }

            val darkModePref = this.findPreference<SwitchPreferenceCompat>("use_dark_mode")!!
            darkModePref.setOnPreferenceChangeListener { _, newValue ->

                val preferences = activity?.getSharedPreferences(activity?.application?.packageName + ShinyPokemonApplication.PREFERENCES_NAME, Context.MODE_PRIVATE)!!

                preferences.edit().putBoolean(ShinyPokemonApplication.PREFERENCES_USE_DARK_MODE, newValue as Boolean).apply()

                activity?.recreate()
                true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}