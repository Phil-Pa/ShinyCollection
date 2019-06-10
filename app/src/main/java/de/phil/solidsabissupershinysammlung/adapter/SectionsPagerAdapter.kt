package de.phil.solidsabissupershinysammlung.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import de.phil.solidsabissupershinysammlung.fragment.MainListFragment
import de.phil.solidsabissupershinysammlung.view.MainView

private val TAB_TITLES = arrayOf(
    "Shiny Liste",
    "Pokemon Liste 1",
    "Pokemon Liste 2",
    "Pokemon Liste 3",
    "Pokemon Liste 4",
    "Pokemon Liste 5"
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val mainView: MainView, private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a MainListFragment (defined as a static inner class below).
        return MainListFragment.newInstance(mainView, position + 1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}