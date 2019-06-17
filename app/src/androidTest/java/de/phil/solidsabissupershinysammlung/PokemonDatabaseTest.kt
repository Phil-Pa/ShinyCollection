package de.phil.solidsabissupershinysammlung

import android.content.Context
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import de.phil.solidsabissupershinysammlung.view.MainView
import junit.framework.Assert
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
    private lateinit var appContext: Context
    private lateinit var mainView: MainView

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getContext()
        val intent = Intent(appContext, MainActivity::class.java)
        activityRule.launchActivity(intent)
        mainView = activityRule.activity as MainView

    }

    private fun clearDatabase() {
        activityRule.runOnUiThread {
            PokemonEngine.deleteAllPokemonInDatabase()
        }
    }

    @Test
    fun testDeleteAllPokemon() {

        val pokemonData = listOf(
            PokemonData("Bisasam", 1, 1, 0, HuntMethod.Other, 0, 0),
            PokemonData("Bisaknosp", 2, 1, 0, HuntMethod.Other, 1, 1)
        )


        activityRule.runOnUiThread {
            for (pokemon in pokemonData)
                PokemonEngine.addPokemon(pokemon)
        }

        assertEquals(2, PokemonEngine.getNumberOfDataSets())

        activityRule.runOnUiThread {
            PokemonEngine.deleteAllPokemonInDatabase()
        }

        assertEquals(0, PokemonEngine.getNumberOfDataSets())

        clearDatabase()
    }

    @Test
    fun testUIAddPokemonToDatabase() {

        val pokemonName = "Bisaflor"
        val encounter = "342"

        onView(withId(R.id.add_pokemon)).perform(click())

        onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(typeText(pokemonName))
        onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(typeText(encounter))
        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_button_add)).perform(click())

        val pokemon = PokemonEngine.getAllPokemonInDatabaseFromTabIndex(0)

        // pokemon got inserted successfully
        assertTrue(pokemon.map { it.name }.filter { it == pokemonName }.size == 1)

        // delete pokemon afterwards
        activityRule.runOnUiThread {
            PokemonEngine.deletePokemonFromDatabaseWithName(pokemonName, 0)
        }

        assert(PokemonEngine.getAllPokemonInDatabaseFromTabIndex(0).isEmpty())

        clearDatabase()
    }

    // claims that we are in main activity
    private fun uiAddPokemon(name: String, encounter: String) {
        onView(withId(R.id.add_pokemon)).perform(click())

        onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(typeText(name))
        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(typeText(encounter))
        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_button_add)).perform(click())
    }

    @Test
    fun testUIDeletePokemonFromDatabase() {

        assert(mainView.getCurrentTabIndex() == 0)

        val pokemonName1 = "Bisaknosp"
        val encounter1 = "1232"

        val pokemonName2 = "Bisasam"
        val encounter2 = "243"

        uiAddPokemon(pokemonName1, encounter1)
        uiAddPokemon(pokemonName2, encounter2)

        assert(mainView.getCurrentTabIndex() == 0)

        activityRule.runOnUiThread {
            PokemonEngine.deletePokemonFromDatabaseWithName(pokemonName1, 0)
        }

        val pokemon = PokemonEngine.getAllPokemonInDatabaseFromTabIndex(0).map { it.name }
        assertFalse(pokemon.contains(pokemonName1))

        clearDatabase()
    }
}
