package de.phil.solidsabissupershinysammlung.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.phil.solidsabissupershinysammlung.fragment.PokemonListFragment
import de.phil.solidsabissupershinysammlung.view.MainView

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
class SectionsPagerAdapter(private val mainView: MainView, private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val pages = mutableMapOf<Int, PokemonListFragment>()

    override fun getItem(position: Int): Fragment {
        if (!pages.keys.contains(position))
            pages[position] = PokemonListFragment.newInstance(mainView, position)

        if (pages[position] == null) {
            // TODO log error

            throw IllegalArgumentException("position")
        }

        return pages[position]!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        return 4
    }
}