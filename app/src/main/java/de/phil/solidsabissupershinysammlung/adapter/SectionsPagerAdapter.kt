package de.phil.solidsabissupershinysammlung.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.fragment.PokemonListFragment

private val TAB_TITLES = arrayOf(
    "Shiny Liste",
    "15er Zyklus",
    "20er Zyklus",
    "SOS"
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    companion object {
        private const val TAG = "SectionsPagerAdapter"
    }

    private val pages = mutableMapOf<Int, PokemonListFragment>()

    override fun getItem(position: Int): Fragment {
        if (!pages.keys.contains(position))
            pages[position] = PokemonListFragment.newInstance(position)

        if (pages[position] == null) {
            Log.e(TAG, "$position as tabIndex is not a key in the pokemon list fragment map")
            throw IllegalArgumentException("position")
        }

        return pages[position]!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        return App.NUM_TAB_VIEWS
    }
}