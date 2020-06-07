package de.phil.solidsabissupershinysammlung.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.phil.solidsabissupershinysammlung.R

class EditPokemonDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTheme()
        setContentView(R.layout.activity_edit_pokemon_data)
    }
}