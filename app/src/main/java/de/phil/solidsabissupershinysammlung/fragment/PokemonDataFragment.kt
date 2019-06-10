package de.phil.solidsabissupershinysammlung.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.adapter.PokemonDataRecyclerViewAdapter
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.MainView

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PokemonDataFragment.OnListFragmentInteractionListener] interface.
 */
class PokemonDataFragment : Fragment() {

    private var mainView: MainView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pokemondata_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)

                val dividerItemDecoration = DividerItemDecoration(
                    view.getContext(),
                    DividerItemDecoration.VERTICAL
                )
                view.addItemDecoration(dividerItemDecoration)

                // get data from the database

                val data = App.getAllPokemonInDatabase()!!

                adapter = PokemonDataRecyclerViewAdapter(data, mainView!!)

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainView) {
            mainView = context
        } else {
            throw RuntimeException("$context must implement MainView")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainView = null
    }
}
