package de.phil.solidsabissupershinysammlung

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PokemonDataFragment.OnListFragmentInteractionListener] interface.
 */
class PokemonDataFragment : Fragment() {

    private var listener: OnListFragmentInteractionListener? = null

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

                adapter = PokemonDataRecyclerViewAdapter(data, listener)

                App.dataChangedListener = object : PokemonListChangedListener {
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
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(data: PokemonData?)
    }
}
