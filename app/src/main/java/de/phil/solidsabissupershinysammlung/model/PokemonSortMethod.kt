package de.phil.solidsabissupershinysammlung.model

enum class PokemonSortMethod {

    InternalId,
    Name,
    PokedexId,
    Encounter;

    companion object {
        private val map = values().associateBy(PokemonSortMethod::ordinal)
        fun fromInt(type: Int) = map[type]
    }

}