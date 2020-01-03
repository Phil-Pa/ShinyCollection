package de.phil.solidsabissupershinysammlung.worker

import de.phil.solidsabissupershinysammlung.database.IPokemonRepository

interface BackgroundDataManager {

    fun import(pokemonRepository: IPokemonRepository, data: String?): Boolean
    fun export(shouldCompressData: Boolean, pokemonRepository: IPokemonRepository): String?

}