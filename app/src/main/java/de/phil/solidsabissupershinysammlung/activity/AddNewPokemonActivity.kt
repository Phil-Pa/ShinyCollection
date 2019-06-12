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

    override fun getPokemonListTabIndex(): Int {
        return intent.getIntExtra("tabIndex", -1)
    }

    override fun getPokedexIdAndGeneration(_name: String): Pair<Int, Int> {
        val generation: Int
        val generationNames: List<String>
        val generationIds: List<Int>

        val name = if (_name.endsWith("-alola"))
            _name.replace("-alola", "")
        else
            _name

        // TODO load pokemon names only once

        val gen1Names = resources.getStringArray(R.array.gen1Names)
        if (!gen1Names.contains(name)) {
            val gen2Names = resources.getStringArray(R.array.gen2Names)
            if (!gen2Names.contains(name)) {
                val gen3Names = resources.getStringArray(R.array.gen3Names)
                if (!gen3Names.contains(name)) {
                    val gen4Names = resources.getStringArray(R.array.gen4Names)
                    if (!gen4Names.contains(name)) {
                        val gen5Names = resources.getStringArray(R.array.gen5Names)
                        if (!gen5Names.contains(name)) {
                            val gen6Names = resources.getStringArray(R.array.gen6Names)
                            if (!gen6Names.contains(name)) {
                                val gen7Names = resources.getStringArray(R.array.gen7Names)
                                if (!gen7Names.contains(name)) {
                                    Toast.makeText(applicationContext, "$name ist nicht im Pokedex vorhanden!", Toast.LENGTH_LONG).show()
                                    return Pair(-1, -1)
                                } else {
                                    generation = 7
                                    generationNames = gen7Names.toList()
                                    generationIds = resources.getIntArray(R.array.gen7Ids).toList()
                                }
                            } else {
                                generation = 6
                                generationNames = gen6Names.toList()
                                generationIds = resources.getIntArray(R.array.gen6Ids).toList()
                            }
                        } else {
                            generation = 5
                            generationNames = gen5Names.toList()
                            generationIds = resources.getIntArray(R.array.gen5Ids).toList()
                        }
                    } else {
                        generation = 4
                        generationNames = gen4Names.toList()
                        generationIds = resources.getIntArray(R.array.gen4Ids).toList()
                    }
                } else {
                    generation = 3
                    generationNames = gen3Names.toList()
                    generationIds = resources.getIntArray(R.array.gen3Ids).toList()
                }
            } else {
                generation = 2
                generationNames = gen2Names.toList()
                generationIds = resources.getIntArray(R.array.gen2Ids).toList()
            }
        } else {
            generation = 1
            generationNames = gen1Names.toList()
            generationIds = resources.getIntArray(R.array.gen1Ids).toList()
        }

        val index = generationNames.indexOf(name)
        val pokedexId = generationIds[index]
        return Pair<Int, Int>(pokedexId, generation)
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
