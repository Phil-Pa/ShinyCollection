package de.phil.solidsabissupershinysammlung.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.PokemonDataRecyclerViewAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.MainView

/**
 * A placeholder fragment containing a simple view.
 */
class PokemonListFragment : Fragment() {

    private lateinit var mainView: MainView
    private var mTabIndex = 0
    private lateinit var recyclerView: RecyclerView

    private var myAdapter: PokemonDataRecyclerViewAdapter? = null
    private var dataList = mutableListOf<PokemonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//       index = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pokemondata_list, container, false)


        if (view is RecyclerView) {
            recyclerView = view

            // get data from the database
            dataList = App.getAllPokemonInDatabaseFromTabIndex(mTabIndex).toMutableList()
            myAdapter = PokemonDataRecyclerViewAdapter(dataList, mainView)

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
                    override fun notifyPokemonAdded(data: PokemonData, tabIndex: Int) {
                        if (mTabIndex == tabIndex) {
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
        fun newInstance(mainView: MainView, sectionNumber: Int): PokemonListFragment {
            val fragment = PokemonListFragment()
            fragment.mainView = mainView
            fragment.mTabIndex = sectionNumber
            fragment.arguments = Bundle().apply {
                putInt(ARG_SECTION_NUMBER, sectionNumber + 1)
            }

            return fragment
        }
    }
}