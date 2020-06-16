package de.phil.shinycollection.model

@Suppress("SpellCheckingInspection")
enum class PokemonEdition(val value: Int) {
    XY(0),
    ORAS(1),
    SM(2),
    USUM(3),
    SWSH(4),
    GO(5),
    LETSGO(6);

    companion object {
        private val map = values().associateBy(PokemonEdition::ordinal)
        fun fromInt(type: Int) = map[type]
    }
}