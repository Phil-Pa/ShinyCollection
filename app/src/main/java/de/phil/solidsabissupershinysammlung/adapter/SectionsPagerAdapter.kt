package de.phil.solidsabissupershinysammlung.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.fragment.PokemonListFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    companion object {
        private const val TAG = "SectionsPagerAdapter"
    }

    private var tabTitles: Array<String> = context.resources.getStringArray(R.array.tab_titles)
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
        return tabTitles[position]
    }

    override fun getCount(): Int {
        return App.NUM_TAB_VIEWS
    }
}