package de.phil.solidsabissupershinysammlung.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.core.AppUtil
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.presenter.AddNewPokemonPresenter
import de.phil.solidsabissupershinysammlung.view.AddNewPokemonView
import de.phil.solidsabissupershinysammlung.databinding.ActivityAddNewPokemonBinding
import kotlinx.android.synthetic.main.activity_add_new_pokemon.*
import java.lang.IllegalArgumentException

class AddNewPokemonActivity : AppCompatActivity(), AddNewPokemonView {

    companion object {

        // if the pokemon names, ids and generations are loaded
        private var internalDataInitialized = false

        private lateinit var gen1Names: Array<String>
        private lateinit var gen2Names: Array<String>
        private lateinit var gen3Names: Array<String>
        private lateinit var gen4Names: Array<String>
        private lateinit var gen5Names: Array<String>
        private lateinit var gen6Names: Array<String>
        private lateinit var gen7Names: Array<String>

        private lateinit var genNamesArray: Array<Array<String>>

        private lateinit var gen1PokedexIds: Array<Int>
        private lateinit var gen2PokedexIds: Array<Int>
        private lateinit var gen3PokedexIds: Array<Int>
        private lateinit var gen4PokedexIds: Array<Int>
        private lateinit var gen5PokedexIds: Array<Int>
        private lateinit var gen6PokedexIds: Array<Int>
        private lateinit var gen7PokedexIds: Array<Int>

        private lateinit var genPokedexIdsArray: Array<Array<Int>>
    }

    init {
        if (!internalDataInitialized) {
            initializeInternalData()
            internalDataInitialized = true
        }
    }

    private fun initializeInternalData() {
        gen1Names = resources.getStringArray(R.array.gen1Names)
        gen2Names = resources.getStringArray(R.array.gen2Names)
        gen3Names = resources.getStringArray(R.array.gen3Names)
        gen4Names = resources.getStringArray(R.array.gen4Names)
        gen5Names = resources.getStringArray(R.array.gen5Names)
        gen6Names = resources.getStringArray(R.array.gen6Names)
        gen7Names = resources.getStringArray(R.array.gen7Names)

        genNamesArray = arrayOf(gen1Names, gen2Names, gen3Names, gen4Names, gen5Names, gen6Names, gen7Names)

        gen1PokedexIds = resources.getIntArray(R.array.gen1Ids).toTypedArray()
        gen2PokedexIds = resources.getIntArray(R.array.gen2Ids).toTypedArray()
        gen3PokedexIds = resources.getIntArray(R.array.gen3Ids).toTypedArray()
        gen4PokedexIds = resources.getIntArray(R.array.gen4Ids).toTypedArray()
        gen5PokedexIds = resources.getIntArray(R.array.gen5Ids).toTypedArray()
        gen6PokedexIds = resources.getIntArray(R.array.gen6Ids).toTypedArray()
        gen7PokedexIds = resources.getIntArray(R.array.gen7Ids).toTypedArray()

        genPokedexIdsArray = arrayOf(gen1PokedexIds, gen2PokedexIds, gen3PokedexIds, gen4PokedexIds, gen5PokedexIds, gen6PokedexIds, gen7PokedexIds)
    }

    override fun getPokemonListTabIndex(): Int {
        return intent.getIntExtra("tabIndex", -1)
    }

    override fun getPokedexIdAndGeneration(_name: String): Pair<Int, Int>? {
        var generation: Int = App.INT_ERROR_CODE

        val name = if (_name.endsWith("-alola"))
            _name.replace("-alola", "")
        else
            _name

        for (i in 0..6) {
            if (genNamesArray[i].contains(name)) {
                generation = i
                break
            }
        }

        // if the pokemon with _name does not exist
        if (generation == App.INT_ERROR_CODE)
            return null

        val index = genNamesArray[generation].toList().indexOf(name)
        val pokedexId = genPokedexIdsArray[generation][index]
        return Pair(pokedexId, generation)
    }

    override fun clearUserInput() {
        add_new_pokemon_activity_edittext_eggsNeeded.text.clear()
        add_new_pokemon_activity_edittext_name.text.clear()
    }

    override fun getEncounters(): Int {
        val encounters = add_new_pokemon_activity_edittext_eggsNeeded.text.toString()
        if (encounters.isEmpty() || encounters.isBlank()) {
            Toast.makeText(applicationContext, "Du musst angeben, wie viele Encounter du gebraucht hast!", Toast.LENGTH_LONG).show()
            return -1
        }
        return encounters.toInt()
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

        App.addNewPokemonView = this
    }
}
