package de.phil.solidsabissupershinysammlung.activity

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.presenter.AddNewPokemonPresenter
import de.phil.solidsabissupershinysammlung.view.AddNewPokemonView
import de.phil.solidsabissupershinysammlung.databinding.ActivityAddNewPokemonBinding
import de.phil.solidsabissupershinysammlung.model.PokemonEngine
import kotlinx.android.synthetic.main.activity_add_new_pokemon.*
import java.lang.IllegalStateException

class AddNewPokemonActivity : AppCompatActivity(), AddNewPokemonView {

    companion object {
        private const val TAG = "AddNewPokemonActivity"
    }

    override fun getPokemonListTabIndex(): Int {
        val tabIndex = intent.getIntExtra("tabIndex", App.INT_ERROR_CODE)
        if (tabIndex == App.INT_ERROR_CODE) {
            Log.e(TAG, "could not receive tabIndex from intent")
            throw IllegalStateException()
        }
        return tabIndex
    }

    override fun getPokedexIdAndGeneration(_name: String): Pair<Int, Int>? {
        var generation: Int = App.INT_ERROR_CODE

        val name = if (_name.endsWith("-alola"))
            _name.replace("-alola", "")
        else
            _name

        for (i in 0..6) {
            if (PokemonEngine.genNamesArray[i].contains(name)) {
                generation = i
                break
            }
        }

        // if the pokemon with _name does not exist
        if (generation == App.INT_ERROR_CODE)
            return null

        val index = PokemonEngine.genNamesArray[generation].toList().indexOf(name)
        val pokedexId = PokemonEngine.genPokedexIdsArray[generation][index]
        return Pair(pokedexId, generation)
    }

    override fun clearUserInput() {
        add_new_pokemon_activity_edittext_eggsNeeded.text.clear()
        add_new_pokemon_activity_edittext_name.text.clear()
    }

    override fun getEncounters(): Int {
        val encounters = add_new_pokemon_activity_edittext_eggsNeeded.text.toString()
        return if (encounters.isEmpty() || encounters.isBlank()) App.INT_ERROR_CODE else encounters.toInt()
    }

    override fun getPokemonName() = add_new_pokemon_activity_edittext_name.text.toString()

    override fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    override fun getSelectedSpinnerPosition(): Int {
        return add_new_pokemon_activity_spinner_hunt_methods.selectedItemPosition
    }

    override fun returnToMainActivity() {
        finish()
    }

    val presenter: AddNewPokemonPresenter = AddNewPokemonPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAddNewPokemonBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_pokemon)
        binding.presenter = presenter
    }
}
