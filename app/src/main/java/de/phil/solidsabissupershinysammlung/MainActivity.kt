package de.phil.solidsabissupershinysammlung

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PokemonDataFragment.OnListFragmentInteractionListener {

    override fun onListFragmentInteraction(data: PokemonData?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.init(applicationContext)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            startActivity(Intent(applicationContext, AddNewPokemon::class.java))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        App.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.show_average_eggs -> {

                val pokemons = App.getAllPokemon()
                if (pokemons != null && pokemons.isNotEmpty()) {
                    val sum = pokemons.sumBy { it.eggsNeeded }
                    val averageEggs = sum.toDouble() / pokemons.size.toDouble()
                    Toast.makeText(applicationContext, averageEggs.toString(), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Du hast noch keine Shiny Pokemon!", Toast.LENGTH_SHORT).show()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
