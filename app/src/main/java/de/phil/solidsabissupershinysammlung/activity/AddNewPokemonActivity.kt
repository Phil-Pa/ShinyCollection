package de.phil.solidsabissupershinysammlung.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.utils.MessageType
import de.phil.solidsabissupershinysammlung.viewmodel.AddNewPokemonViewModel
import kotlinx.android.synthetic.main.activity_add_new_pokemon.*
import javax.inject.Inject


class AddNewPokemonActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    companion object {

        const val INTENT_EXTRA_HUNT_METHOD = "hunt_method"
        const val INTENT_EXTRA_NAME = "name"
        const val INTENT_EXTRA_ENCOUNTERS = "encounter_needed"
        const val INTENT_EXTRA_POKEDEX_ID = "pokedex_id"
        const val INTENT_EXTRA_GENERATION = "generation"
        const val INTENT_EXTRA_TAB_INDEX = "tab_index"
        const val INTENT_EXTRA_INTERNAL_ID = "internal_id"

    }

    private lateinit var viewModel: AddNewPokemonViewModel
    private var addedPokemon = false

    private var tabIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_pokemon)

        tabIndex = intent.getIntExtra(INTENT_EXTRA_TAB_INDEX, -1)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddNewPokemonViewModel::class.java)

        add_new_pokemon_activity_button_add.setOnClickListener {

            val name = add_new_pokemon_activity_edittext_name.text.toString()

            val encountersKnown = add_new_pokemon_activity_checkbox_encounter_known.isChecked

            val encountersNeededText = add_new_pokemon_activity_edittext_eggsNeeded.text

            val encounters =
                if (encountersKnown &&
                    encountersNeededText != null &&
                    encountersNeededText.isNotEmpty() &&
                    encountersNeededText.isNotBlank()
                )
                    encountersNeededText.toString().toInt() else App.ENCOUNTER_UNKNOWN
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
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            viewModel.getPokemonNames()
        )
        add_new_pokemon_activity_edittext_name.setAdapter(adapter)
        add_new_pokemon_activity_edittext_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()

                val isAlola = text.endsWith(App.ALOLA_EXTENSION)
                if (isAlola)
                    text = text.replace(App.ALOLA_EXTENSION, "")

                val isGalar = text.endsWith(App.GALAR_EXTENSION)
                if (isGalar)
                    text = text.replace(App.GALAR_EXTENSION, "")

                if (viewModel.pokemonNameExists(text)) {

                    // needed to get the correct download url
                    val id = viewModel.getPokedexIdByName(text)
                    val generation = when {
                        !isAlola && !isGalar -> viewModel.getGenerationByName(text)
                        !isGalar -> 7
                        !isAlola -> 8
                        else -> throw Exception()
                    }

                    val invalidData = PokemonData("-1", id, generation, -1, HuntMethod.Other, -1)
                    val urlWithoutAlola = invalidData.getDownloadUrl()
                    val url = StringBuilder(urlWithoutAlola)

                    when {
                        isAlola -> url.insert(urlWithoutAlola.length - 4, App.ALOLA_EXTENSION)
                        isGalar -> url.insert(urlWithoutAlola.length - 4, App.GALAR_EXTENSION)
                    }

                    val downloadUrl = url.toString()

                    Glide.with(this@AddNewPokemonActivity)
                        .load(downloadUrl)
                        .placeholder(ContextCompat.getDrawable(this@AddNewPokemonActivity, R.drawable.placeholder_pokemon))
                        .into(add_new_pokemon_activity_imageView_preview)

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

        addedPokemon = true

        intent.putExtra(INTENT_EXTRA_HUNT_METHOD, pokemonData.huntMethod.ordinal)
        intent.putExtra(INTENT_EXTRA_NAME, pokemonData.name)
        intent.putExtra(INTENT_EXTRA_ENCOUNTERS, pokemonData.encounterNeeded)
        intent.putExtra(INTENT_EXTRA_POKEDEX_ID, pokemonData.pokedexId)
        intent.putExtra(INTENT_EXTRA_GENERATION, pokemonData.generation)
        intent.putExtra(INTENT_EXTRA_TAB_INDEX, pokemonData.tabIndex)
        intent.putExtra(INTENT_EXTRA_INTERNAL_ID, pokemonData.internalId)

        setResult(App.REQUEST_ADD_POKEMON, intent)
        finish()
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

}
