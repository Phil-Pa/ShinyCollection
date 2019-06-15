package de.phil.solidsabissupershinysammlung.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.core.AppUtil
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.toGerman
import de.phil.solidsabissupershinysammlung.model.toJapanese
import de.phil.solidsabissupershinysammlung.view.MainView
import kotlinx.android.synthetic.main.fragment_pokemondata.view.*
import java.lang.Exception
import java.lang.IllegalStateException

class PokemonDataRecyclerViewAdapter(
    private val mValues: MutableList<PokemonData>,
    private val mainView: MainView
) : RecyclerView.Adapter<PokemonDataRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pokemondata, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        // "ID: "
        holder.mPokedexIdView.text = (holder.mPokedexIdView.text.toString() + ": " + item.pokedexId.toString())
        // "Name: "
        holder.mNameView.text = (holder.mNameView.text.toString() + ": " + item.name)
        // "Encounter: "
        holder.mEggsNeededView.text = (holder.mEggsNeededView.text.toString() + ": " + item.encounterNeeded.toString())

        // "Methode: "
        val method: String = item.huntMethod.toString()

        holder.mHuntMethodView.text = (holder.mHuntMethodView.text.toString() + ": " + method)
        holder.mShinyImageView.setImageBitmap(AppUtil.getDrawableFromURL(item.getDownloadUrl()))

        with(holder.mView) {
            tag = item
            setOnLongClickListener{
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
