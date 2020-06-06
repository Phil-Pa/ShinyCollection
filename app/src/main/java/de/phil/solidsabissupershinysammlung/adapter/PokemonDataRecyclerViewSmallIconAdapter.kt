package de.phil.solidsabissupershinysammlung.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.activity.IPokemonListActivity
import de.phil.solidsabissupershinysammlung.model.PokemonData
import kotlinx.android.synthetic.main.fragment_pokemondata_small_icon.view.*

class PokemonDataRecyclerViewSmallIconAdapter(
    private val mValues: List<PokemonData>, private val activity: IPokemonListActivity
) : RecyclerView.Adapter<PokemonDataRecyclerViewSmallIconAdapter.ViewHolder>() {


    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mShinyImageView: ImageView = mView.fragment_pokemondata_small_icon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pokemondata_small_icon, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        Glide.with(activity.getContext())
            .load(item.getDownloadUrl())
            .placeholder(ContextCompat.getDrawable(activity.getContext(), R.drawable.placeholder_pokemon))
            .into(holder.mShinyImageView)
    }

}