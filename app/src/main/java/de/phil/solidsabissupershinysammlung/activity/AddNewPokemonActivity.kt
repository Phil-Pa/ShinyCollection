package de.phil.solidsabissupershinysammlung.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.core.AppUtil
import de.phil.solidsabissupershinysammlung.database.AndroidPokemonResources
import de.phil.solidsabissupershinysammlung.database.PokemonRepository
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.utils.MessageType
import de.phil.solidsabissupershinysammlung.utils.showMessage
import de.phil.solidsabissupershinysammlung.viewmodel.AddNewPokemonViewModel
import kotlinx.android.synthetic.main.activity_add_new_pokemon.*

class AddNewPokemonActivity : AppCompatActivity() {

    private lateinit var viewModel: AddNewPokemonViewModel

    private var tabIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_pokemon)

        // TODO: don't hardcode
        tabIndex = intent.getIntExtra("tabIndex", -1)

        val androidPokemonResources = AndroidPokemonResources(this)

        viewModel = ViewModelProviders.of(this).get(AddNewPokemonViewModel::class.java)
        viewModel.init(PokemonRepository(androidPokemonResources, application))

        add_new_pokemon_activity_button_add.setOnClickListener {

            val name = add_new_pokemon_activity_edittext_name.text.toString()

            val encountersKnown = add_new_pokemon_activity_checkbox_encounter_known.isChecked

            val encounters =
                if (encountersKnown) add_new_pokemon_activity_edittext_eggsNeeded.text.toString().toInt() else App.ENCOUNTER_UNKNOWN
            val huntMethod =
                HuntMethod.fromInt(add_new_pokemon_activity_spinner_hunt_methods.selectedItemPosition)!!


            val pokemonData = PokemonData(name, -1, -1, encounters, huntMethod, tabIndex)

            val result = viewModel.validateInput(pokemonData)

            if (result.first == null && result.second != null) {
                setActivityResult(result.second!!)
            } else {
                showMessage(result.first!!, MessageType.Error)
            }


        }
        add_new_pokemon_activity_checkbox_encounter_known.setOnCheckedChangeListener { _, isChecked ->
            add_new_pokemon_activity_edittext_eggsNeeded.isEnabled = isChecked
        }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, androidPokemonResources.getPokemonNames())
        add_new_pokemon_activity_edittext_name.setAdapter(adapter)
        add_new_pokemon_activity_edittext_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()

                val isAlola = text.endsWith("-alola")
                if (isAlola)
                    text = text.replace("-alola", "")



                if (viewModel.pokemonNameExists(text)) {

                    // needed to get the correct download url
                    val id = viewModel.getPokedexIdByName(text)

                    val invalidData = PokemonData("-1", id, -1, -1, HuntMethod.Other, -1)
                    val urlWithoutAlola = invalidData.getDownloadUrl()
                    val url = StringBuilder(urlWithoutAlola)

                    val bitmap = if (isAlola)
                        AppUtil.getDrawableFromURL(
                            url.insert(
                                urlWithoutAlola.length - 4,
                                "-alola"
                            ).toString()
                        )
                    else
                        AppUtil.getDrawableFromURL(urlWithoutAlola)

                    add_new_pokemon_activity_imageView_preview.setImageBitmap(bitmap)
                } else {
                    add_new_pokemon_activity_imageView_preview.setImageBitmap(null)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setActivityResult(pokemonData: PokemonData) {
        val intent = Intent()

        // TODO: don't hardcode
        intent.putExtra("huntMethod", pokemonData.huntMethod.ordinal)
        intent.putExtra("name", pokemonData.name)
        intent.putExtra("encounters", pokemonData.encounterNeeded)
        intent.putExtra("pokedexId", pokemonData.pokedexId)
        intent.putExtra("generation", pokemonData.generation)
        intent.putExtra("tabIndex", pokemonData.tabIndex)
        intent.putExtra("internalId", pokemonData.internalId)

        setResult(App.REQUEST_ADD_POKEMON, intent)
        finish()
    }

}
