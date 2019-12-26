package de.phil.solidsabissupershinysammlung.model

enum class PokemonEdition(val value: Int) {
    ORAS(0),
    SM(1),
    USUM(2),
    SWSH(3),
    GO(4),
    LETSGO(5);

    companion object {
        private val map = values().associateBy(PokemonEdition::ordinal)
        fun fromInt(type: Int) = map[type]

//        fun getPokemonEditionUpTo(pokemonEdition: PokemonEdition): List<PokemonEdition> {
//            return when (pokemonEdition) {
//                ORAS -> listOf(ORAS)
//                SM -> listOf(ORAS, SM)
//                USUM -> listOf(ORAS, SM, USUM)
//                SWSH -> listOf(ORAS, SM, USUM, SWSH)
//
//            }
//        }
    }
}