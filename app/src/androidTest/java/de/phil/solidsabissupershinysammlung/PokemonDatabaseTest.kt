package de.phil.solidsabissupershinysammlung

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
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

    @Test
    fun testAlreadyInserted() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("de.phil.solidsabissupershinysammlung", appContext.packageName)

        val intent = Intent(appContext, MainActivity::class.java)
        activityRule.launchActivity(intent)

        val pokemonName = "Bisasam"
        val encounter = "123"

        for (i in 1..2) {
            onView(withId(R.id.add_pokemon)).perform(click())

            onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(typeText(pokemonName))
            onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

            onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(typeText(encounter))
            onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(closeSoftKeyboard())

            onView(withId(R.id.add_new_pokemon_activity_button_add)).perform(click())
        }

        // check if the same data got only inserted once
        val pokemon = PokemonEngine.getAllPokemonInDatabaseFromTabIndex(0)

        var counter = 0

        for (p in pokemon) {
            if (p.name == pokemonName && p.encounterNeeded == encounter.toInt())
                counter++
        }

        if (counter >= 2)
            fail("Pokemon with same data got added twice or more to the database")
    }
}
