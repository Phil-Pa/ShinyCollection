package de.phil.solidsabissupershinysammlung.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import de.phil.solidsabissupershinysammlung.adapter.PokemonDataRecyclerViewAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod

/**
 * fragment in the main view pager
 */
class PokemonListFragment : Fragment() {

    /**
     * specifies what tab this fragment is in the view pager
     */
    private var mTabIndex = 0

    /**
     * the main view of the fragment
     */
    private lateinit var recyclerView: RecyclerView

    private var myAdapter: PokemonDataRecyclerViewAdapter? = null
    private var dataList = mutableListOf<PokemonData>()

    /**
     * sort the data in the @see [recyclerView]
     */
    private fun sortData(sortMethod: PokemonSortMethod) {
        when (sortMethod) {
            PokemonSortMethod.InternalId -> dataList.sortBy { it.internalId }
            PokemonSortMethod.Name -> dataList.sortBy { it.name }
            PokemonSortMethod.PokedexId -> dataList.sortBy { it.pokedexId }
            PokemonSortMethod.Encounter -> {

                val zeros = dataList.filter { it.encounterNeeded == App.ENCOUNTER_UNKNOWN }

                if (zeros.isEmpty()) {
                    dataList.sortBy { it.encounterNeeded }
                    return
                } else {
                    val other = dataList.filter { it.encounterNeeded != App.ENCOUNTER_UNKNOWN }.toMutableList()

                    other.sortBy { it.encounterNeeded }
                    for (i in zeros.indices)
                        other.add(zeros[i])

                    dataList.clear()
                    for (i in other)
                        dataList.add(i)
                }
            }
        }
    }

    private fun getMainActivity(): MainActivity {
        return (activity as MainActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        val view = inflater.inflate(R.layout.fragment_pokemondata_list, container, false)

        if (view is RecyclerView) {
            recyclerView = view

            // get data from the database
            val liveData = getMainActivity().viewModel.getAllPokemonDataFromTabIndex(mTabIndex)
            dataList = liveData.toMutableList()

            // sort the data
            sortData(getMainActivity().viewModel.getSortMethod())

            myAdapter = PokemonDataRecyclerViewAdapter(dataList, getMainActivity())

            with(recyclerView) {
                layoutManager = GridLayoutManager(context, if (activity!!.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2)

                val dividerItemDecoration = DividerItemDecoration(
                    view.getContext(),
                    DividerItemDecoration.VERTICAL
                )

                recyclerView.addItemDecoration(dividerItemDecoration)

                adapter = myAdapter

                getMainActivity().addRecyclerViewChangedListener(object : MainActivity.OnListChangedListener {
                    override fun refreshRecyclerView() {
                        recyclerView.requestLayout()
                        myAdapter?.notifyDataSetChanged()
                    }

                    override fun addPokemon(pokemonData: PokemonData) {
                        if (mTabIndex == pokemonData.tabIndex) {
                            dataList.add(pokemonData)
                            myAdapter?.notifyItemInserted(dataList.size - 1)
                        }
                    }

                    override fun updatePokemonEncounter(pokemonData: PokemonData) {
                        if (mTabIndex == pokemonData.tabIndex) {
                            val position = dataList.indexOfFirst { it.internalId == pokemonData.internalId }
                            myAdapter?.notifyItemChanged(position)
                        }
                    }

                    override fun deletePokemon(pokemonData: PokemonData) {
                        if (mTabIndex == pokemonData.tabIndex) {
                            val position = dataList.indexOf(pokemonData)
                            dataList.removeAt(position)
                            myAdapter?.notifyItemRemoved(position)
                        }
                    }

                    override fun deleteAllPokemon(tabIndex: Int) {
                        if (mTabIndex == tabIndex) {
                            val length = dataList.size
                            dataList.clear()
                            myAdapter?.notifyItemRangeRemoved(0, length)
                        }
                    }

                    override fun sort(pokemonSortMethod: PokemonSortMethod) {
                        sortData(pokemonSortMethod)
                        myAdapter?.notifyDataSetChanged()
                    }

                })
            }
        }
        return view
    }

    companion object {

        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): PokemonListFragment {
            val fragment = PokemonListFragment()
            with (fragment) {
                mTabIndex = sectionNumber
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber + 1)
                }
            }

            return fragment
        }
    }
}