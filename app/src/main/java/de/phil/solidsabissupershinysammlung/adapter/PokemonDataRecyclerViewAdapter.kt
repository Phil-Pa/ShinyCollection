package de.phil.solidsabissupershinysammlung.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.activity.MainActivity
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.toGerman
import kotlinx.android.synthetic.main.fragment_pokemondata.view.*

class PokemonDataRecyclerViewAdapter(
    private val mValues: MutableList<PokemonData>, private val activity: MainActivity
) : RecyclerView.Adapter<PokemonDataRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pokemondata, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        // "Edition: "
        holder.mPokemonEditionView.text = ("Edition: " + item.pokemonEdition.toString())

        // "ID: "
        holder.mPokedexIdView.text = ("ID: " + item.pokedexId.toString())
        // "Name: "
        holder.mNameView.text = (item.name)
        // "Encounter: "

        if (item.encounterNeeded == App.ENCOUNTER_UNKNOWN)
            holder.mEggsNeededView.text = activity.resources.getString(R.string.encounter_unknown)
        else
            holder.mEggsNeededView.text = (activity.resources.getString(R.string.encounter_colon) + " " + item.encounterNeeded.toString())

        // "Methode: "
        val method: String = item.huntMethod.toGerman()

        holder.mHuntMethodView.text = (activity.resources.getString(R.string.method_colon) + " " + method)

        Glide.with(activity)
            .load(item.getDownloadUrl())
            .placeholder(ContextCompat.getDrawable(activity, R.drawable.placeholder_pokemon))
            .into(holder.mShinyImageView)

        with(holder.mView) {
            tag = item
            setOnLongClickListener{
                activity.onListEntryLongClick(it.tag as PokemonData)
                true
            }
            setOnClickListener {
                activity.onListEntryClick(it.tag as PokemonData)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mPokemonEditionView: TextView = mView.fragment_pokemondata_edition
        val mPokedexIdView: TextView = mView.fragment_pokemondata_pokedex_id
        val mNameView: TextView = mView.fragment_pokemondata_name
        val mEggsNeededView: TextView = mView.fragment_pokemondata_eggs_needed
        val mHuntMethodView: TextView = mView.fragment_pokemondata_hunt_method
        val mShinyImageView: ImageView = mView.fragment_pokemondata_shiny_image

    }
}