package de.phil.solidsabissupershinysammlung

import android.content.Context
import java.lang.StringBuilder

object App {

    private var pokemonDatabase: PokemonDatabase? = null
    private var config: BaseConfig? = null

    fun init(context: Context) {
        config = BaseConfig(context)
        pokemonDatabase = PokemonDatabase()

        pokemonDatabase?.init(context)

        if (config?.firstStart == true) {
            pokemonDatabase?.create()
            config?.firstStart = false
        }
    }

    fun getAllPokemonInDatabase() : List<PokemonData>? = pokemonDatabase?.getAllPokemon()

    fun addPokemonToDatabase(data: PokemonData) = pokemonDatabase?.insert(data)

    fun getAllPokemonNames(context: Context) : List<String> {
        val names1 = context.resources.getStringArray(R.array.gen1Names)
        val names2 = context.resources.getStringArray(R.array.gen2Names)
        val names3 = context.resources.getStringArray(R.array.gen3Names)
        val names4 = context.resources.getStringArray(R.array.gen4Names)
        val names5 = context.resources.getStringArray(R.array.gen5Names)
        val names6 = context.resources.getStringArray(R.array.gen6Names)
        val names7 = context.resources.getStringArray(R.array.gen7Names)

        val result = mutableListOf<String>()
        for (i in names1)
            result.add(i)

        for (i in names2)
            result.add(i)

        for (i in names3)
            result.add(i)

        for (i in names4)
            result.add(i)

        for (i in names5)
            result.add(i)

        for (i in names6)
            result.add(i)

        for (i in names7)
            result.add(i)

        return result
    }

    fun finish() {
        pokemonDatabase?.close()
    }

}