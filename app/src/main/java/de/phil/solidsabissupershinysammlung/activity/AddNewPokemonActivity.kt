package de.phil.solidsabissupershinysammlung.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.ShinyPokemonApplication
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.INTENT_EXTRA_TAB_INDEX
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.utils.MessageType
import de.phil.solidsabissupershinysammlung.viewmodel.AddNewPokemonViewModel
import kotlinx.android.synthetic.main.activity_add_new_pokemon.*


class AddNewPokemonActivity : AppCompatActivity() {

    private lateinit var viewModel: AddNewPokemonViewModel

    private var tabIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTheme()
        setContentView(R.layout.activity_add_new_pokemon)

        tabIndex = intent.getIntExtra(INTENT_EXTRA_TAB_INDEX, -1)

        viewModel = ViewModelProvider(this).get(AddNewPokemonViewModel::class.java)

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
                    encountersNeededText.toString().toInt() else ShinyPokemonApplication.ENCOUNTER_UNKNOWN
            val huntMethod =
                HuntMethod.fromInt(add_new_pokemon_activity_spinner_hunt_methods.selectedItemPosition)!!

            val pokemonEdition = PokemonEdition.fromInt(add_new_pokemon_activity_spinner_pokemon_editions.selectedItemPosition)!!

            val pokemonData = PokemonData(name, -1, -1, encounters, huntMethod, pokemonEdition, tabIndex)

            val (success, message) = viewModel.addPokemonToDatabase(pokemonData)

            if (!success) {
                showMessage(message, MessageType.Error)
            } else {
                showMessage(message, MessageType.Success)
                finish()
            }


        }
        add_new_pokemon_activity_checkbox_encounter_known.setOnCheckedChangeListener { _, isChecked ->
            add_new_pokemon_activity_edittext_eggsNeeded.isEnabled = isChecked
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            viewModel.getPokemonNames()
        )

        add_new_pokemon_activity_edittext_name.setAdapter(adapter)
        add_new_pokemon_activity_edittext_name.setOnItemClickListener { parent, view, position, id ->
            hideKeyboard()
        }
        add_new_pokemon_activity_edittext_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()

                val isAlola = text.endsWith(ShinyPokemonApplication.ALOLA_EXTENSION)
                if (isAlola)
                    text = text.replace(ShinyPokemonApplication.ALOLA_EXTENSION, "")

                val isGalar = text.endsWith(ShinyPokemonApplication.GALAR_EXTENSION)
                if (isGalar)
                    text = text.replace(ShinyPokemonApplication.GALAR_EXTENSION, "")

                if (viewModel.pokemonNameExists(text)) {

                    // needed to get the correct download url
                    val id = viewModel.getPokedexIdByName(text)
                    val generation = when {
                        !isAlola && !isGalar -> viewModel.getGenerationByName(text)
                        !isGalar -> 7
                        !isAlola -> 8
                        else -> throw Exception()
                    }

                    val invalidData = PokemonData("-1", id, generation, -1, HuntMethod.Other, PokemonEdition.ORAS, -1)
                    val urlWithoutAlola = invalidData.getDownloadUrl()
                    val url = StringBuilder(urlWithoutAlola)

                    when {
                        isAlola -> url.insert(urlWithoutAlola.length - 4, ShinyPokemonApplication.ALOLA_EXTENSION)
                        isGalar -> url.insert(urlWithoutAlola.length - 4, ShinyPokemonApplication.GALAR_EXTENSION)
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

}
