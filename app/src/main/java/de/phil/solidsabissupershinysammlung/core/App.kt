package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import de.phil.solidsabissupershinysammlung.database.PokemonDatabase
import de.phil.solidsabissupershinysammlung.fragment.PokemonListChangedListener
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.AddNewPokemonView
import de.phil.solidsabissupershinysammlung.view.MainView
import kotlin.IllegalStateException

object App {

    const val INT_ERROR_CODE = -1

    val shinyList: List<PokemonData>
        get() {
            return getAllPokemonInDatabase()!!
        }
    val pokemonList1 = mutableListOf<PokemonData>()
    val pokemonList2 = mutableListOf<PokemonData>()

    private var pokemonDatabase: PokemonDatabase? = null
    private var config: BaseConfig? = null

    private var pokemonNamesList = listOf<String>()

    private var mMainView: MainView? = null

    var mainView get() = mMainView
    set(value) {
        if (mMainView == null)
            mMainView = value
    }

    private var mAddNewPokemonView: AddNewPokemonView? = null

    var addNewPokemonView get() = mAddNewPokemonView
        set(value) {
            if (mAddNewPokemonView == null)
                mAddNewPokemonView = value
        }

    // ?
    val dataChangedListeners = java.util.ArrayList<PokemonListChangedListener>(4)

    fun init(context: Context) {
        config = BaseConfig(context)
        pokemonDatabase = PokemonDatabase()

        pokemonDatabase?.init(context)

        if (config?.firstStart == true) {
            pokemonDatabase?.create()
            config?.firstStart = false
        }

        // init pokemon names
        pokemonNamesList = AppUtil.getAllPokemonNames(context)

//        config?.firstStart = true
    }

    private fun updateShinyStatistics() {
        mainView?.updateShinyStatistics()
    }

    fun getAllPokemonNames() = pokemonNamesList

    fun getAllPokemonAlolaNames(): List<String> {
        return listOf(
            "Rattfratz-alola", "Rattikarl-alola",
            "Raichu-alola", "Sandan-alola", "Sandamer-alola",
            "Vulpix-alola", "Vulnona-alola", "Digda-alola", "Digdri-alola",
            "Mauzi-alola", "Snobilikat-alola", "Kleinstein-alola", "Georok-alola",
            "Geowaz-alola", "Sleima-alola", "Sleimok-alola", "Kokowei-alola", "Knogga-alola")
    }

    fun getAllPokemonInDatabase(): List<PokemonData>? = pokemonDatabase?.getAllPokemon()

    fun getTotalNumberOfShinys() = pokemonDatabase?.getAllPokemonOfTabIndex(0)!!.size

    fun getAverageEggsCount(): Double {
        val pokemon = pokemonDatabase?.getAllPokemonOfTabIndex(0)
        var pokemonCount = 0
        return if (pokemon != null && pokemon.isNotEmpty()) {
            var sum = 0
            for (p in pokemon) {
                if (p.huntMethod == HuntMethod.Hatch) {
                    sum += p.encounterNeeded
                    pokemonCount++
                }
            }
            sum.toDouble() / pokemonCount.toDouble()
        } else
            0.0
    }

    fun getTotalEggsCount(): Int {
        val pokemon = pokemonDatabase?.getAllPokemonOfTabIndex(0) ?: return 0

        var eggsCount = 0

        for (p in pokemon)
            if (p.huntMethod == HuntMethod.Hatch)
                eggsCount += p.encounterNeeded

        return eggsCount
    }

    fun finish() {
        pokemonDatabase?.close()
    }

    fun deletePokemonFromDatabase(data: PokemonData, tabIndex: Int) {

        val pokemon = getAllPokemonInDatabaseFromTabIndex(tabIndex)

        var position: Int = 0
        var found = false

        for (i in 0 until pokemon.size) {
            if (data == pokemon[i]) {
                position = i
                found = true
                break
            }
        }

        if (!found)
            throw IllegalStateException()

        pokemonDatabase?.delete(data, tabIndex)

        dataChangedListeners[tabIndex].notifyPokemonDeleted(tabIndex, position)

    }

    fun addPokemon(data: PokemonData, tabIndex: Int) {
        pokemonDatabase?.insert(data, tabIndex)
        dataChangedListeners[tabIndex].notifyPokemonAdded(data, tabIndex)
        updateShinyStatistics()
    }

    fun getAllPokemonInDatabaseFromTabIndex(tabIndex: Int): List<PokemonData> {

        return pokemonDatabase?.getAllPokemonOfTabIndex(tabIndex)!!

    }

}