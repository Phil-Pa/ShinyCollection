package de.phil.solidsabissupershinysammlung.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.PokemonDataRecyclerViewAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.MainView

/**
 * A placeholder fragment containing a simple view.
 */
class MainListFragment : Fragment() {

    private lateinit var mainView: MainView
    private var tabIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//       index = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pokemondata_list, container, false)

        // TODO use recycler view adapter to initialize the page

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)

                val dividerItemDecoration = DividerItemDecoration(
                    view.getContext(),
                    DividerItemDecoration.VERTICAL
                )
                view.addItemDecoration(dividerItemDecoration)

                // get data from the database

                var data = listOf<PokemonData>()
                if (tabIndex == 0)
                    data = App.getAllPokemonInDatabase()!!

                adapter = PokemonDataRecyclerViewAdapter(data, mainView)

                App.dataChangedListener = object :
                    PokemonListChangedListener {
                    override fun notifyPokemonAdded() {
                        adapter?.notifyItemRangeChanged(1, adapter?.itemCount!!)
                    }

                    override fun notifyPokemonDeleted() {
                        adapter?.notifyItemRangeChanged(1, adapter?.itemCount!!)
                    }

                }
            }
        }

        return view
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(mainView: MainView, sectionNumber: Int): MainListFragment {
            val fragment = MainListFragment()
            fragment.mainView = mainView
            fragment.tabIndex = sectionNumber - 1
            fragment.arguments = Bundle().apply {
                putInt(ARG_SECTION_NUMBER, sectionNumber)
            }

            return fragment
        }
    }
}