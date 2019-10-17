package de.phil.solidsabissupershinysammlung.presenter

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainPresenterTest {

    @get:Rule
    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)
    private lateinit var appContext: Context
    private lateinit var mainView: MainView
    private lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(appContext, MainActivity::class.java)
        activityRule.launchActivity(intent)
        mainView = activityRule.activity as MainView
        presenter = MainPresenter(mainView)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getAverageEggsCount() {
    }

    @Test
    fun getTotalEggsCount() {
    }

    @Test
    fun getTotalNumberOfShinys() {
    }

    @Test
    fun deletePokemonFromDatabase() {
    }

    @Test
    fun importData() {
    }

    @Test
    fun exportData() {

        presenter.startAddNewPokemonActivity()

        val pokemonName = "Bisaflor"
        val encounter = "342"

        onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(
            ViewActions.typeText(
                pokemonName
            )
        )
        onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(ViewActions.closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(
            ViewActions.typeText(
                encounter
            )
        )
        onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(ViewActions.closeSoftKeyboard())

        onView(withId(R.id.add_new_pokemon_activity_button_add)).perform(click())

        presenter.exportData()

        val data = mainView.getClipboardStringData()

        if (data == null) {
            fail()
        }

        if (data != null) {
            assert(data.contains(pokemonName))
            assert(data.contains(encounter))
        }
    }
}