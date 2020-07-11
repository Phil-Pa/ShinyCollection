package de.phil.shinycollection.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import de.phil.shinycollection.R
import de.phil.shinycollection.activity.IPokemonListActivity
import de.phil.shinycollection.activity.MainActivity
import de.phil.shinycollection.adapter.PokemonDataAdapter
import de.phil.shinycollection.adapter.PokemonDataRecyclerViewAdapter
import de.phil.shinycollection.adapter.PokemonDataRecyclerViewSmallIconAdapter
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.model.PokemonData
import de.phil.shinycollection.model.PokemonSortMethod

/**
 * fragment in the main view pager. every activity using this fragment needs to implement [IPokemonListActivity]
 */
class PokemonListFragment : Fragment() {

    /**
     * specifies what tab this fragment is in the view pager
     */
    private var mTabIndex = 0
    private var showSmallIcon = true
    private lateinit var recyclerView: RecyclerView
    private val masterAdapter = PokemonDataAdapter()
    private var dataList = mutableListOf<PokemonData>()

    private fun sortDataInRecyclerView(sortMethod: PokemonSortMethod) {
        when (sortMethod) {
            PokemonSortMethod.InternalId -> dataList.sortBy { it.internalId }
            PokemonSortMethod.Name -> dataList.sortBy { it.name }
            PokemonSortMethod.PokedexId -> dataList.sortBy { it.pokedexId }
            PokemonSortMethod.Encounter -> {

                val zeros = dataList.filter { it.encounterNeeded == ShinyPokemonApplication.ENCOUNTER_UNKNOWN }

                if (zeros.isEmpty()) {
                    dataList.sortBy { it.encounterNeeded }
                    return
                } else {
                    val other = dataList.filter { it.encounterNeeded != ShinyPokemonApplication.ENCOUNTER_UNKNOWN }.toMutableList()

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

    private fun getPokemonListActivity(): IPokemonListActivity {
        return activity as IPokemonListActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true

        val view = inflater.inflate(R.layout.fragment_pokemondata_list, container, false)

        if (view is RecyclerView) {
            recyclerView = view

            loadData()

            with(recyclerView) {
                layoutManager = if (showSmallIcon) {
                    GridLayoutManager(context, 4).apply {
                        orientation = HORIZONTAL
                    }
                } else {
                    GridLayoutManager(context, if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 1 else 2)
                }

                adapter = if (showSmallIcon)
                    masterAdapter.smallIconAdapter
                else
                    masterAdapter.normalAdapter

                if (showSmallIcon) {
                    recyclerView.background = null
                    val layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.WRAP_CONTENT,
                        RecyclerView.LayoutParams.MATCH_PARENT
                    )
                    layoutParams.setMargins(0, 0, 0, 0)
                    recyclerView.layoutParams = layoutParams
                    recyclerView.isVerticalScrollBarEnabled = false
                    recyclerView.isHorizontalScrollBarEnabled = true
                    return view
                }

                val dividerItemDecoration = DividerItemDecoration(
                    view.getContext(),
                    DividerItemDecoration.VERTICAL
                )

                recyclerView.addItemDecoration(dividerItemDecoration)

                getPokemonListActivity().addRecyclerViewChangedListener(object : MainActivity.OnListChangedListener {
                    override fun refreshRecyclerView() {
                        recyclerView.requestLayout()
                        masterAdapter.normalAdapter?.notifyDataSetChanged()
                    }

                    override fun addPokemon(pokemonData: PokemonData) {
                        if (mTabIndex == pokemonData.tabIndex) {
                            dataList.add(pokemonData)
                            masterAdapter.normalAdapter?.notifyItemInserted(dataList.size - 1)
                        }
                    }

                    override fun reload() {
                        // reload data and update adapter
                        loadData()

                        // set new adapter
                        adapter = masterAdapter.normalAdapter
                    }

                    override fun updatePokemonEncounter(pokemonData: PokemonData) {
                        if (mTabIndex == pokemonData.tabIndex) {
                            val position = dataList.indexOfFirst { it.internalId == pokemonData.internalId }
                            masterAdapter.normalAdapter?.notifyItemChanged(position)
                        }
                    }

                    override fun deletePokemon(pokemonData: PokemonData) {
                        if (mTabIndex == pokemonData.tabIndex) {
                            val position = dataList.indexOf(pokemonData)
                            dataList.removeAt(position)
                            masterAdapter.normalAdapter?.notifyItemRemoved(position)
                        }
                    }

                    override fun deleteAllPokemon(tabIndex: Int) {
                        if (mTabIndex == tabIndex) {
                            val length = dataList.size
                            dataList.clear()
                            masterAdapter.normalAdapter?.notifyItemRangeRemoved(0, length)
                        }
                    }

                    override fun sort(pokemonSortMethod: PokemonSortMethod) {
                        sortDataInRecyclerView(pokemonSortMethod)
                        masterAdapter.normalAdapter?.notifyDataSetChanged()
                    }

                })
            }
        }
        return view
    }

    private fun loadData() {
        // get data from the database
        val data = getPokemonListActivity().getAllPokemonDataFromTabIndex(mTabIndex)
        dataList = data.toMutableList()

        // sort the data
        sortDataInRecyclerView(getPokemonListActivity().getSortMethod())

        if (showSmallIcon)
            masterAdapter.smallIconAdapter = PokemonDataRecyclerViewSmallIconAdapter(dataList, getPokemonListActivity())
        else
            masterAdapter.normalAdapter = PokemonDataRecyclerViewAdapter(dataList, getPokemonListActivity())
    }

    companion object {

        @JvmStatic
        fun newInstance(sectionNumber: Int, showSmallIcon: Boolean): PokemonListFragment {
            val fragment = PokemonListFragment()
            fragment.mTabIndex = sectionNumber
            fragment.showSmallIcon = showSmallIcon

            return fragment
        }
    }
}