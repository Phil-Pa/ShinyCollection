package de.phil.solidsabissupershinysammlung

import android.view.Gravity
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import de.phil.solidsabissupershinysammlung.adapter.PokemonDataRecyclerViewAdapter
import de.phil.solidsabissupershinysammlung.core.App
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SampleTest {

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    private fun delay(millis: Long = 100) = Thread.sleep(millis)
    private val delayTime: Long = 200

    @Test
    fun testAdd() {

        for (i in 0 until App.NUM_TAB_VIEWS) {
            val beforeData = activityRule.activity.viewModel.getAllPokemonDataFromTabIndex(i)

            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            delay(delayTime)

            onView(withText(activityRule.activity.getString(R.string.menu_add))).perform(click())

            delay(delayTime)

            onView(withId(R.id.add_new_pokemon_activity_edittext_name)).perform(replaceText("Bisasam"))

            delay(delayTime)
            onView(withId(R.id.add_new_pokemon_activity_edittext_eggsNeeded)).perform(typeText("123"))
            androidx.test.espresso.Espresso.closeSoftKeyboard()

            delay(delayTime)

            onView(withId(R.id.add_new_pokemon_activity_button_add)).perform(click())

            delay(delayTime)
            val afterData = activityRule.activity.viewModel.getAllPokemonDataFromTabIndex(i)

            assert(beforeData.size + 1 == afterData.size)

            onView(withId(R.id.view_pager)).perform(swipeLeft())
            delay(delayTime)
        }
    }

    @Test
    fun testDelete() {

        for (i in 0 until App.NUM_TAB_VIEWS) {

            val beforeData = activityRule.activity.viewModel.getAllPokemonDataFromTabIndex(i)

            onView(withIndex(withId(R.id.pokemon_recycler_view), i))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<PokemonDataRecyclerViewAdapter.ViewHolder>(
                        0,
                        longClickChildViewWithId(R.id.fragment_pokemondata_view)
                    )
                )

            delay(1000)

            onView(withId(R.id.delete_entry)).perform(click())

            delay(1000)

            onView(withText("Bestätigen")).perform(click())

            delay(1000)

            val afterData = activityRule.activity.viewModel.getAllPokemonDataFromTabIndex(i)

            assert(beforeData.size == afterData.size + 1)

            onView(withId(R.id.view_pager)).perform(swipeLeft())

            delay(100)
        }

    }

    @Test
    fun testDataImport() {
        activityRule.activity.copyToClipboard(TestData.CLIPBOARD_DATA)

        delay(500)

        onView(withId(R.id.drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open())

        delay(500)

        onView(withText(activityRule.activity.getString(R.string.drawer_import_data))).perform(click())

        delay(5000)
    }

    private fun longClickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v.performLongClick()
            }
        }
    }

    private fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var currentIndex = 0

            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }

}