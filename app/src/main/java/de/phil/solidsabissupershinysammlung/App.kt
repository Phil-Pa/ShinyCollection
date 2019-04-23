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

    fun getAllPokemonInDatabase() : List<PokemonData>? = pokemonDatabase?.getAllPokemon()

    fun addPokemonToDatabase(data: PokemonData) = pokemonDatabase?.insert(data)

    fun getAverageEggsCount() : Double {
        val pokemon = getAllPokemonInDatabase()
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

    fun finish() {
        pokemonDatabase?.close()
    }

    fun deletePokemonFromDatabase(data: PokemonData) = pokemonDatabase?.delete(data)

}