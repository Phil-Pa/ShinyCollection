package de.phil.solidsabissupershinysammlung.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.phil.solidsabissupershinysammlung.fragment.MainListFragment
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

    private val pages = mutableMapOf<Int, MainListFragment>()

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a MainListFragment (defined as a static inner class below).
        if (!pages.keys.contains(position))
            pages[position] = MainListFragment.newInstance(mainView, position)

        return pages[position]!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        return 4
    }
}