package de.phil.solidsabissupershinysammlung.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.core.AppUtil
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.model.PokemonData
import kotlinx.android.synthetic.main.activity_add_new_pokemon.*

class AddNewPokemonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_pokemon)

        // presenter method
        add_new_pokemon_activity_button_add.setOnClickListener {

            val position = add_new_pokemon_activity_spinner_hunt_methods.selectedItemPosition

            val huntMethod = HuntMethod.fromInt(position)
            val name = add_new_pokemon_activity_edittext_name.text.toString()

            if (name.isEmpty() || name.isEmpty()) {
                Toast.makeText(applicationContext, "Du musst einen Namen für das Pokemon eingeben!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                val allNames = AppUtil.getAllPokemonNames(applicationContext)
                if (!allNames.contains(name)) {
                    Toast.makeText(applicationContext, "Es gibt kein Pokemon namens $name!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            val eggsNeededString = add_new_pokemon_activity_edittext_eggsNeeded.text.toString()
            if (eggsNeededString.isEmpty() || eggsNeededString.isBlank()) {
                Toast.makeText(applicationContext, "Du musst angeben, wie viele Encounter du gebraucht hast!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val eggsNeeded = eggsNeededString.toInt()

            // get pokedexId and generation from inputs
            val generation: Int
            val generationNames: List<String>
            val generationIds: List<Int>

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
                                        return@setOnClickListener
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

            add_new_pokemon_activity_edittext_eggsNeeded.text.clear()
            add_new_pokemon_activity_edittext_name.text.clear()

            val data = PokemonData(
                name,
                pokedexId,
                generation,
                eggsNeeded,
                huntMethod!!
            )
            App.addPokemonToDatabase(data)

            finish()
        }

    }
}
