package de.phil.solidsabissupershinysammlung.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import de.phil.solidsabissupershinysammlung.adapter.PokemonDataRecyclerViewAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
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
//                layoutManager = LinearLayoutManager(context)
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

                    override fun deletePokemon(tabIndex: Int, pokemonData: PokemonData) {
                        if (mTabIndex == tabIndex) {
                            // TODO: check if the position is correct
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
        private const val TAG = "PokemonListFragment"

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