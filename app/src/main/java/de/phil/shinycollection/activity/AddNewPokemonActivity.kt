package de.phil.shinycollection.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import de.phil.shinycollection.R
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.ShinyPokemonApplication.Companion.INVALID_VALUE
import de.phil.shinycollection.ShinyPokemonApplication.Companion.TAB_INDEX_BREEDING
import de.phil.shinycollection.ShinyPokemonApplication.Companion.TAB_INDEX_OTHER
import de.phil.shinycollection.ShinyPokemonApplication.Companion.TAB_INDEX_SHINY_LIST
import de.phil.shinycollection.ShinyPokemonApplication.Companion.TAB_INDEX_SOFTRESET
import de.phil.shinycollection.ShinyPokemonApplication.Companion.TAB_INDEX_SOS
import de.phil.shinycollection.adapter.PokemonAutoCompleteFilterAdapter
import de.phil.shinycollection.model.*
import de.phil.shinycollection.utils.MessageType
import de.phil.shinycollection.viewmodel.AddNewPokemonViewModel
import kotlinx.android.synthetic.main.activity_add_new_pokemon.*


class AddNewPokemonActivity : AppCompatActivity() {

    private lateinit var viewModel: AddNewPokemonViewModel
    private var tabIndex = INVALID_VALUE
    private lateinit var pokemonEdition: PokemonEdition
    private val addedSpinnerOptions = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTheme()
        setContentView(R.layout.activity_add_new_pokemon)

        finishIfIntentExtraInvalid()
        setupPassedIntentExtras()

        viewModel = ViewModelProvider(this).get(AddNewPokemonViewModel::class.java)

        setupEncounterKnownCheckBox()
        setupAddPokemonButton()
        setupEditTextName()
        setupHuntMethodSpinner()
    }

    private fun setupHuntMethodSpinner() {
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)

        val spinnerOptions = resources.getStringArray(R.array.hunt_methods).toMutableList()

        when (tabIndex) {
            TAB_INDEX_SHINY_LIST -> addedSpinnerOptions.addAll(spinnerOptions)
            TAB_INDEX_BREEDING -> addedSpinnerOptions.add(spinnerOptions[0])
            TAB_INDEX_SOS -> addedSpinnerOptions.add(spinnerOptions[1])
            TAB_INDEX_SOFTRESET -> addedSpinnerOptions.add(spinnerOptions[2])
            TAB_INDEX_OTHER -> {
                for (i in 1..3)
                    spinnerOptions.removeAt(0)

                addedSpinnerOptions.addAll(spinnerOptions)
            }
        }

        spinnerAdapter.addAll(addedSpinnerOptions)

        add_new_pokemon_activity_spinner_hunt_methods.adapter = spinnerAdapter
    }

    private fun setupPassedIntentExtras() {
        tabIndex = intent.getIntExtra(INTENT_EXTRA_TAB_INDEX, INVALID_VALUE)
        pokemonEdition = PokemonEdition.fromInt(
            intent.getIntExtra(
                INTENT_EXTRA_POKEMON_EDITION,
                PokemonEdition.XY.value
            )
        )!!
    }

    private fun finishIfIntentExtraInvalid() {
        if (intent.getIntExtra(
                INTENT_EXTRA_POKEMON_EDITION,
                INVALID_VALUE
            ) == INVALID_VALUE || intent.getIntExtra(
                INTENT_EXTRA_TAB_INDEX,
                INVALID_VALUE
            ) == INVALID_VALUE
        ) {
            showMessage(getString(R.string.internal_error), MessageType.Error)
            finish()
        }
    }

    private fun textIsEntered(text: Editable?): Boolean {
        return text != null &&
                text.isNotEmpty() &&
                text.isNotBlank()
    }

    private fun setupAddPokemonButton() {
        add_new_pokemon_activity_button_add.setOnClickListener {

            val name = add_new_pokemon_activity_edittext_name.text.toString()
            val areEncountersKnown = add_new_pokemon_activity_checkbox_encounter_known.isChecked
            val encountersNeededText = add_new_pokemon_activity_edittext_eggsNeeded.text

            val encounters = if (areEncountersKnown && textIsEntered(encountersNeededText)) {
                encountersNeededText.toString().toInt()
            }
            else  {
                ShinyPokemonApplication.ENCOUNTER_UNKNOWN
            }

            val huntMethodName = addedSpinnerOptions[add_new_pokemon_activity_spinner_hunt_methods.selectedItemPosition]
            val huntMethod = HuntMethod.fromString(huntMethodName)

            val pokemonData = PokemonData(name, INVALID_VALUE, INVALID_VALUE, encounters, huntMethod, pokemonEdition, tabIndex)

            val (success, message) = viewModel.addPokemonToDatabase(pokemonData)

            if (!success) {
                showMessage(message, MessageType.Error)
            } else {
                showMessage(message, MessageType.Success)
                finish()
            }


        }
    }

    private fun setupEditTextName() {
        val adapter = PokemonAutoCompleteFilterAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            viewModel.getPokemonNamesFormsInclusive()
        )

        add_new_pokemon_activity_edittext_name.setAdapter(adapter)
        add_new_pokemon_activity_edittext_name.setOnItemClickListener { _, _, _, _ -> hideKeyboard() }

        add_new_pokemon_activity_edittext_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()

                // TODO: refactor
                val isAlola = text.endsWith(ShinyPokemonApplication.ALOLA_EXTENSION)
                if (isAlola)
                    text = text.replace(ShinyPokemonApplication.ALOLA_EXTENSION, "")

                val isGalar = text.endsWith(ShinyPokemonApplication.GALAR_EXTENSION)
                if (isGalar)
                    text = text.replace(ShinyPokemonApplication.GALAR_EXTENSION, "")

                if (viewModel.pokemonNameExists(text)) {
                    loadPokemonImage(text, isAlola, isGalar)
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

    private fun setupEncounterKnownCheckBox() {
        add_new_pokemon_activity_checkbox_encounter_known.setOnCheckedChangeListener { _, isChecked ->
            add_new_pokemon_activity_edittext_eggsNeeded.isEnabled = isChecked
        }
    }

    private fun loadPokemonImage(pokemonName: String, isAlola: Boolean, isGalar: Boolean) {
        // needed to get the correct download url
        val id = viewModel.getPokedexIdByName(pokemonName)
        val generation = when {
            !isAlola && !isGalar -> viewModel.getGenerationByName(pokemonName)
            !isGalar -> 7
            !isAlola -> 8
            else -> throw Exception()
        }

        val downloadUrl = PokemonData.getDownloadUrl(generation, id, isAlola, isGalar)

        Glide.with(this@AddNewPokemonActivity)
            .load(downloadUrl)
            .placeholder(ContextCompat.getDrawable(this@AddNewPokemonActivity, R.drawable.placeholder_pokemon))
            .into(add_new_pokemon_activity_imageView_preview)
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
