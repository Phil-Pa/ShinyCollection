package de.phil.solidsabissupershinysammlung.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.core.AppUtil
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.presenter.AddNewPokemonPresenter
import de.phil.solidsabissupershinysammlung.view.AddNewPokemonView
import kotlinx.android.synthetic.main.activity_add_new_pokemon.*

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
            if (App.pokemonEngine.getNamesArray(i).contains(name)) {
                generation = i
                break
            }
        }

        // if the pokemon with _name does not exist
        if (generation == App.INT_ERROR_CODE)
            return null

        val index = App.pokemonEngine.getNamesArray(generation).toList().indexOf(name)
        val pokedexId = App.pokemonEngine.getPokedexIdsArray(generation)[index]
        return Pair(pokedexId, generation)
    }

    override fun clearUserInput() {
        add_new_pokemon_activity_edittext_eggsNeeded.text?.clear()
        add_new_pokemon_activity_edittext_name.text?.clear()

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

    private val presenter: AddNewPokemonPresenter = AddNewPokemonPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_pokemon)
        add_new_pokemon_activity_button_add.setOnClickListener {
            presenter.addPokemonButtonClick()
        }
        add_new_pokemon_activity_edittext_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var text = s.toString()

                val isAlola = text.endsWith("-alola")
                if (isAlola)
                    text = text.replace("-alola", "")

                if (App.pokemonEngine.pokemonNameExists(text)) {
                    val id = App.pokemonEngine.getPokedexIdByName(text)

                    if (id == App.INT_ERROR_CODE)
                        return

                    val invalidData = PokemonData(-1, "-1", id, -1, -1, HuntMethod.Other, -1)
                    val urlWithoutAlola = invalidData.getDownloadUrl()
                    val url = StringBuilder(urlWithoutAlola)

                    val bitmap = if (isAlola)
                        AppUtil.getDrawableFromURL(url.insert(urlWithoutAlola.length - 4, "-alola").toString())
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

}
