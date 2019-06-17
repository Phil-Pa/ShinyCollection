package de.phil.solidsabissupershinysammlung.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.PokemonDataRecyclerViewAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

class PokemonListFragment : Fragment() {

    private var mTabIndex = 0

    private lateinit var recyclerView: RecyclerView

    private var myAdapter: PokemonDataRecyclerViewAdapter? = null
    private var dataList = mutableListOf<PokemonData>()

    private fun sortData(sortMethod: PokemonSortMethod) {
        when (sortMethod) {
            PokemonSortMethod.InternalId -> dataList.sortBy { it.internalId }
            PokemonSortMethod.Name -> dataList.sortBy { it.name }
            PokemonSortMethod.PokedexId -> dataList.sortBy { it.pokedexId }
            PokemonSortMethod.Encounter -> dataList.sortBy { it.encounterNeeded }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pokemondata_list, container, false)

        if (view is RecyclerView) {
            recyclerView = view

            // get data from the database
            dataList = PokemonEngine.getAllPokemonInDatabaseFromTabIndex(mTabIndex).toMutableList()

            // sort the data
            sortData(App.getSortMethod())


            myAdapter = PokemonDataRecyclerViewAdapter(dataList, App.mainView)

            with(recyclerView) {
                layoutManager = LinearLayoutManager(context)

                val dividerItemDecoration = DividerItemDecoration(
                    view.getContext(),
                    DividerItemDecoration.VERTICAL
                )
                recyclerView.addItemDecoration(dividerItemDecoration)

                adapter = myAdapter

                App.dataChangedListeners.add(mTabIndex, object :
                    PokemonListChangedListener {
                    override fun notifySortPokemon(sortMethod: PokemonSortMethod) {
                        sortData(sortMethod)
                        myAdapter?.notifyDataSetChanged()
                    }

                    override fun notifyPokemonAdded(data: PokemonData) {
                        if (mTabIndex == data.tabIndex) {
                            dataList.add(data)
                            myAdapter?.notifyItemInserted(dataList.size - 1)
                        }
                    }

                    override fun notifyPokemonDeleted(tabIndex: Int, position: Int) {
                        if (mTabIndex == tabIndex) {
                            dataList.removeAt(position)
                            myAdapter?.notifyItemRemoved(position)
                        }
                    }

                    override fun notifyAllPokemonDeleted(tabIndex: Int) {
                        if (mTabIndex == tabIndex) {
                            val length = dataList.size
                            dataList.clear()
                            myAdapter?.notifyItemRangeRemoved(0, length)
                        }
                    }
                })
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
        fun newInstance(sectionNumber: Int): PokemonListFragment {
            val fragment = PokemonListFragment()
            fragment.mTabIndex = sectionNumber
            fragment.arguments = Bundle().apply {
                putInt(ARG_SECTION_NUMBER, sectionNumber + 1)
            }

            return fragment
        }
    }
}