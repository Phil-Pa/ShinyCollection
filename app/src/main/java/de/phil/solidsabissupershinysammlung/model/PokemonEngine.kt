package de.phil.solidsabissupershinysammlung.model

import android.content.Context
import android.util.Log
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.database.PokemonDatabase

object PokemonEngine : IPokemonEngine {

    override fun deleteAllPokemonInDatabase() {
        if (pokemonDatabase.getNumberOfDataSets() == 0)
            return

        pokemonDatabase.deleteAll()
        for (i in 0 until App.NUM_TAB_VIEWS) {
            App.dataChangedListeners[i].notifyAllPokemonDeleted(i)
        }
    }

    override fun finish() {
        pokemonDatabase.close()
    }

    private const val TAG = "PokemonEngine"
    private lateinit var pokemonDatabase: PokemonDatabase

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

    override fun initialize(context: Context) {

        initializeVariables(context)
        initializeDatabase(context)

    }

    private fun initializeDatabase(context: Context) {
        pokemonDatabase = PokemonDatabase()

        Log.i(TAG, "initialize database connection")
        pokemonDatabase.init(context)

        if (App.config.firstStart)
            pokemonDatabase.create()
    }

    private fun initializeVariables(context: Context) {
        if (internalDataInitialized)
            return

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

        internalDataInitialized = true
    }

    override fun getAllPokemonAlolaNames() = listOf(
        "Rattfratz-alola", "Rattikarl-alola",
        "Raichu-alola", "Sandan-alola", "Sandamer-alola",
        "Vulpix-alola", "Vulnona-alola", "Digda-alola", "Digdri-alola",
        "Mauzi-alola", "Snobilikat-alola", "Kleinstein-alola", "Georok-alola",
        "Geowaz-alola", "Sleima-alola", "Sleimok-alola", "Kokowei-alola", "Knogga-alola")

    override fun getAllPokemonNames() = genNamesArray.flatten()

    override fun addPokemon(data: PokemonData) {
        pokemonDatabase.insert(data)
        App.dataChangedListeners[data.tabIndex].notifyPokemonAdded(data)
    }

    override fun getTotalNumberOfShinys() = pokemonDatabase.getAllPokemonOfTabIndex(0).size

    override fun getAverageEggsCount(): Double {
        val pokemon = pokemonDatabase.getAllPokemonOfTabIndex(0)
        var pokemonCount = 0
        return if (pokemon.isNotEmpty()) {
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

    // TODO make one variable function for deleting pokemon

    override fun deletePokemonFromDatabase(data: PokemonData) {
        val pokemon = getAllPokemonInDatabaseFromTabIndex(data.tabIndex).toMutableList()

        when (App.getSortMethod()) {
            PokemonSortMethod.InternalId -> pokemon.sortBy { it.internalId }
            PokemonSortMethod.Name -> pokemon.sortBy { it.name }
            PokemonSortMethod.PokedexId -> pokemon.sortBy { it.pokedexId }
            PokemonSortMethod.Encounter -> pokemon.sortBy { it.encounterNeeded }
        }

        var position = App.INT_ERROR_CODE

        for (i in 0 until pokemon.size) {
            if (data == pokemon[i]) {
                position = i
                break
            }
        }

        if (position == App.INT_ERROR_CODE) {
            Log.e(TAG, "$data is not in the database")
            throw IllegalStateException()
        }

        pokemonDatabase.delete(data)
        App.dataChangedListeners[data.tabIndex].notifyPokemonDeleted(data.tabIndex, position)
    }

    override fun deletePokemonFromDatabaseWithName(pokemonName: String, tabIndex: Int) {
        val pokemon = getAllPokemonInDatabaseFromTabIndex(tabIndex)

        var position = App.INT_ERROR_CODE

        for (i in 0 until pokemon.size) {
            if (pokemonName == pokemon[i].name) {
                position = i
                break
            }
        }

        if (position == App.INT_ERROR_CODE) {
            Log.e(TAG, "A pokemon with the name $pokemonName is not in the database")
            throw IllegalStateException()
        }

        pokemonDatabase.delete(pokemonName, tabIndex)
        App.dataChangedListeners[tabIndex].notifyPokemonDeleted(tabIndex, position)
    }

    override fun getTotalEggsCount(): Int {
        val pokemon = pokemonDatabase.getAllPokemonOfTabIndex(0)

        var eggsCount = 0

        for (p in pokemon)
            if (p.huntMethod == HuntMethod.Hatch)
                eggsCount += p.encounterNeeded

        return eggsCount
    }

    override fun getTotalNumberOfEggShiny(): Int {
        val pokemon = pokemonDatabase.getAllPokemonOfTabIndex(0)

        var eggShinyCount = 0

        for (p in pokemon)
            if (p.huntMethod == HuntMethod.Hatch)
                eggShinyCount++

        return eggShinyCount
    }

    override fun getTotalNumberOfSosShinys(): Int {
        val pokemon = pokemonDatabase.getAllPokemonOfTabIndex(0)

        var sosShinyCount = 0

        for (p in pokemon)
            if (p.huntMethod == HuntMethod.SOS)
                sosShinyCount++

        return sosShinyCount
    }

    override fun getAllPokemonInDatabaseFromTabIndex(tabIndex: Int) = pokemonDatabase.getAllPokemonOfTabIndex(tabIndex)

    override fun getMaxInternalId() = pokemonDatabase.getMaxInternalId()

    override fun getNumberOfDataSets() = pokemonDatabase.getNumberOfDataSets()
}