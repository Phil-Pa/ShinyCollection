package de.phil.solidsabissupershinysammlung

enum class HuntMethod {
    Hatch,
    SOS,
    FriendSafari,
    SoftReset,
    Random,
    DexNav,
    Hordes,
    PokeRadar,
    RNGManipulation,
    ChainFinishing,
    UltraDimension;

    companion object {
        private val map = values().associateBy(HuntMethod::ordinal)
        fun fromInt(type: Int) = map[type]
    }

}

fun HuntMethod.toGerman(): String {
    return when (this.name) {
        "Hatch" -> "Gezüchtet"
        "SOS" -> "SOS-Methode"
        "FriendSafari" -> "Kontaktsafari"
        "SoftReset" -> "Softreset"
        "Random" -> "Zufall"
        "DexNav" -> "DexNav"
        "Hordes" -> "Massenbegegnung"
        "PokeRadar" -> "PokeRadar"
        "RNGManipulation" -> "RNGManipulation"
        "ChainFishing" -> "Chain Fishing"
        "UltraDimension" -> "Ultradimension"
        else -> "???"
    }
}