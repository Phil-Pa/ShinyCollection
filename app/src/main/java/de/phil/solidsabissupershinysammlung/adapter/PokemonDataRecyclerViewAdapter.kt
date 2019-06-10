package de.phil.solidsabissupershinysammlung.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.phil.solidsabissupershinysammlung.core.AppUtil
import de.phil.solidsabissupershinysammlung.R

import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.toGerman
import de.phil.solidsabissupershinysammlung.view.MainView

import kotlinx.android.synthetic.main.fragment_pokemondata.view.*

class PokemonDataRecyclerViewAdapter(
    private val mValues: List<PokemonData>,
    private val mainView: MainView
) : RecyclerView.Adapter<PokemonDataRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pokemondata, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mPokedexIdView.text = ("ID: " + item.pokedexId.toString())
        holder.mNameView.text = ("Name: " + item.name)
        holder.mEggsNeededView.text = ("Encounter: " + item.encounterNeeded.toString())

        val method = item.huntMethod.toGerman()

        holder.mHuntMethodView.text = ("Methode: $method")

        holder.mShinyImageView.setImageBitmap(AppUtil.getDrawableFromURL(item.getDownloadUrl()))

        with(holder.mView) {
            tag = item
            setOnLongClickListener{
                // Notify the active callbacks interface (the activity, if the fragment is attached to
                // one) that an item has been selected.
                mainView.onListEntryLongClick(it.tag as PokemonData)

                true
            }
            setOnClickListener {
                mainView.onListEntryClick(it.tag as PokemonData)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mPokedexIdView: TextView = mView.fragment_pokemondata_pokedex_id
        val mNameView: TextView = mView.fragment_pokemondata_name
        val mEggsNeededView: TextView = mView.fragment_pokemondata_eggs_needed
        val mHuntMethodView: TextView = mView.fragment_pokemondata_hunt_method

        val mShinyImageView: ImageView = mView.fragment_pokemondata_shiny_image

    }
}
