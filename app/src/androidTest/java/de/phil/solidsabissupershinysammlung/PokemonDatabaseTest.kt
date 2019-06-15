package de.phil.solidsabissupershinysammlung

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PokemonDatabaseTest {

    @get:Rule
    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        val appContext = InstrumentationRegistry.getContext()
        val intent = Intent(appContext, MainActivity::class.java)
        activityRule.launchActivity(intent)

    }

    @Test
    fun testAddPokemonToDatabase() {

        val pokemonName = "Bisaflor"
        val encounter = "342"

        onView(withId(R.id.add_pokemon)).perform(click())

        onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(typeText(pokemonName))
        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(typeText(encounter))
        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_button_add)).perform(click())

        val pokemon = PokemonEngine.getAllPokemonInDatabaseFromTabIndex(0)

        // pokemon got inserted successfully
        assertTrue(pokemon.map { it.name }.filter { it == pokemonName }.size == 1)

        // delete pokemon afterwards
        try {
            PokemonEngine.deletePokemonFromDatabaseWithName(pokemonName, 0)
        } catch (e: Exception) {

        }
    }

    @Test
    fun testDeletePokemonFromDatabase() {

        val pokemonName = "Bisaknosp"
        val encounter = "1232"

        onView(withId(R.id.add_pokemon)).perform(click())

        onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(typeText(pokemonName))
        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(typeText(encounter))
        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_button_add)).perform(click())

        try {
            PokemonEngine.deletePokemonFromDatabaseWithName(pokemonName, 0)
        } catch (e: Exception) {

        }

        val pokemon = PokemonEngine.getAllPokemonInDatabaseFromTabIndex(0).map { it.name }
        assertFalse(pokemon.contains(pokemonName))
    }
}
