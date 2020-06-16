package de.phil.shinycollection.adapter

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.phil.shinycollection.R
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.fragment.PokemonListFragment

class SectionsPagerAdapter(context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private const val TAG = "SectionsPagerAdapter"
    }

    private val pages = mutableMapOf<Int, PokemonListFragment>()
    private var tabTitles: Array<String> = context.resources.getStringArray(R.array.tab_titles)

    override fun getItem(position: Int): Fragment {

        if (position < 0 || position >= ShinyPokemonApplication.NUM_TAB_VIEWS)
            throw IllegalArgumentException("invalid position $position, less than 0 or greater than maximum number of tabs in view pager")

        if (!pages.keys.contains(position))
            pages[position] = PokemonListFragment.newInstance(position)

        return pages[position]!!
    }

    override fun getPageTitle(position: Int) = tabTitles[position]
    override fun getCount() = ShinyPokemonApplication.NUM_TAB_VIEWS

}