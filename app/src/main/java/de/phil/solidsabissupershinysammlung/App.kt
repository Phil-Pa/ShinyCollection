package de.phil.solidsabissupershinysammlung

import android.content.Context

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

    fun getAllPokemon() : List<PokemonData>? = pokemonDatabase?.getAllPokemon()

    fun finish() {
        pokemonDatabase?.close()
    }

}