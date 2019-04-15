package de.phil.solidsabissupershinysammlung

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PokemonDataFragment.OnListFragmentInteractionListener {

    override fun onListFragmentInteraction(data: PokemonData?) {

        startSupportActionMode(object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.delete_entry -> {
                        if (data != null) {
                            App.deletePokemonFromDatabase(data)
                            mode?.finish()
                        }
                    }
                }
                return true
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(R.menu.menu_actions, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {

            }

        })

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

                val pokemons = App.getAllPokemonInDatabase()
                var pokemonCount = 0
                if (pokemons != null && pokemons.isNotEmpty()) {
                    var sum = 0
                    for (p in pokemons) {
                        if (p.huntMethod == HuntMethod.Hatch) {
                            sum += p.encounterNeeded
                            pokemonCount++
                        }
                    }
                    val averageEggs = sum.toDouble() / pokemonCount.toDouble()
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
