package de.phil.solidsabissupershinysammlung.core

import android.content.Context
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.database.PokemonDatabase
import de.phil.solidsabissupershinysammlung.fragment.PokemonListChangedListener
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.view.AddNewPokemonView
import de.phil.solidsabissupershinysammlung.view.MainView
import kotlin.IllegalStateException

object App {

    // if the pokemon names, ids and generations are loaded
    private var internalDataInitialized = false

    private lateinit var gen1Names: Array<String>
    private lateinit var gen2Names: Array<String>
    private lateinit var gen3Names: Array<String>
    private lateinit var gen4Names: Array<String>
    private lateinit var gen5Names: Array<String>
    private lateinit var gen6Names: Array<String>
    private lateinit var gen7Names: Array<String>

    lateinit var genNamesArray: Array<Array<String>>

    private lateinit var gen1PokedexIds: Array<Int>
    private lateinit var gen2PokedexIds: Array<Int>
    private lateinit var gen3PokedexIds: Array<Int>
    private lateinit var gen4PokedexIds: Array<Int>
    private lateinit var gen5PokedexIds: Array<Int>
    private lateinit var gen6PokedexIds: Array<Int>
    private lateinit var gen7PokedexIds: Array<Int>

    lateinit var genPokedexIdsArray: Array<Array<Int>>

    const val INT_ERROR_CODE = -1

    private var pokemonDatabase: PokemonDatabase? = null
    private var config: BaseConfig? = null

    private var mMainView: MainView? = null

    var mainView get() = mMainView
    set(value) {
        if (mMainView == null)
            mMainView = value
    }

    val dataChangedListeners = java.util.ArrayList<PokemonListChangedListener>(4)

    fun init(context: Context) {
        config = BaseConfig(context)
        pokemonDatabase = PokemonDatabase()

        pokemonDatabase?.init(context)

        if (config?.firstStart == true) {
            pokemonDatabase?.create()
            config?.firstStart = false
        }

        if (!internalDataInitialized) {
            initializeInternalData(context)
            internalDataInitialized = true
        }
    }

    private fun initializeInternalData(context: Context) {
        gen1Names = context.resources.getStringArray(R.array.gen1Names)
        gen2Names = context.resources.getStringArray(R.array.gen2Names)
        gen3Names = context.resources.getStringArray(R.array.gen3Names)
        gen4Names = context.resources.getStringArray(R.array.gen4Names)
        gen5Names = context.resources.getStringArray(R.array.gen5Names)
        gen6Names = context.resources.getStringArray(R.array.gen6Names)
        gen7Names = context.resources.getStringArray(R.array.gen7Names)

        genNamesArray = arrayOf(gen1Names, gen2Names, gen3Names, gen4Names, gen5Names, gen6Names, gen7Names)

        gen1PokedexIds = context.resources.getIntArray(R.array.gen1Ids).toTypedArray()
        gen2PokedexIds = context.resources.getIntArray(R.array.gen2Ids).toTypedArray()
        gen3PokedexIds = context.resources.getIntArray(R.array.gen3Ids).toTypedArray()
        gen4PokedexIds = context.resources.getIntArray(R.array.gen4Ids).toTypedArray()
        gen5PokedexIds = context.resources.getIntArray(R.array.gen5Ids).toTypedArray()
        gen6PokedexIds = context.resources.getIntArray(R.array.gen6Ids).toTypedArray()
        gen7PokedexIds = context.resources.getIntArray(R.array.gen7Ids).toTypedArray()

        genPokedexIdsArray = arrayOf(gen1PokedexIds, gen2PokedexIds, gen3PokedexIds, gen4PokedexIds, gen5PokedexIds, gen6PokedexIds, gen7PokedexIds)
    }

    private fun updateShinyStatistics() {
        // TODO log
        mainView?.updateShinyStatistics()
    }

    fun getAllPokemonNames() = genNamesArray.flatten()

    fun getAllPokemonAlolaNames(): List<String> {
        return listOf(
            "Rattfratz-alola", "Rattikarl-alola",
            "Raichu-alola", "Sandan-alola", "Sandamer-alola",
            "Vulpix-alola", "Vulnona-alola", "Digda-alola", "Digdri-alola",
            "Mauzi-alola", "Snobilikat-alola", "Kleinstein-alola", "Georok-alola",
            "Geowaz-alola", "Sleima-alola", "Sleimok-alola", "Kokowei-alola", "Knogga-alola")
    }

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

        var position = INT_ERROR_CODE

        for (i in 0 until pokemon.size) {
            if (data == pokemon[i]) {
                position = i
                break
            }
        }

        if (position == App.INT_ERROR_CODE) {
            // TODO log error
            throw IllegalStateException()
        }

        pokemonDatabase?.delete(data, tabIndex)
        dataChangedListeners[tabIndex].notifyPokemonDeleted(tabIndex, position)
    }

    fun addPokemon(data: PokemonData, tabIndex: Int) {
        pokemonDatabase?.insert(data, tabIndex)
        dataChangedListeners[tabIndex].notifyPokemonAdded(data, tabIndex)
        updateShinyStatistics()
    }

    fun getAllPokemonInDatabaseFromTabIndex(tabIndex: Int) = pokemonDatabase?.getAllPokemonOfTabIndex(tabIndex)!!

}