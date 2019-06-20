package de.phil.solidsabissupershinysammlung.presenter

import android.content.Context
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import de.phil.solidsabissupershinysammlung.view.MainView
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainPresenterTest {

    @get:Rule
    val activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)
    private lateinit var appContext: Context
    private lateinit var mainView: MainView

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().context
        val intent = Intent(appContext, MainActivity::class.java)
        activityRule.launchActivity(intent)
        mainView = activityRule.activity as MainView
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
    }
}