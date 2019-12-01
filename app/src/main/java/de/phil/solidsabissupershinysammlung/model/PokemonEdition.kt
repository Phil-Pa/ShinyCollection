package de.phil.solidsabissupershinysammlung.model

enum class PokemonEdition(val value: Int) {
    ORAS(0),
    SM(1),
    USUM(2),
    SWSH(3);

    companion object {
        private val map = values().associateBy(PokemonEdition::ordinal)
        fun fromInt(type: Int) = map[type]
    }
}