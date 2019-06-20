package de.phil.solidsabissupershinysammlung.model

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
    UltraDimension,
    Other;

    companion object {
        private val map = values().associateBy(HuntMethod::ordinal)
        fun fromInt(type: Int) = map[type]
    }

}

fun HuntMethod.toGerman(): String {
    return when (this) {
        HuntMethod.Hatch -> "Gezüchtet"
        HuntMethod.SOS -> "SOS-Methode"
        HuntMethod.FriendSafari -> "Kontaktsafari"
        HuntMethod.SoftReset -> "Softreset"
        HuntMethod.Random -> "Zufall"
        HuntMethod.DexNav -> "DexNav"
        HuntMethod.Hordes -> "Massenbegegnung"
        HuntMethod.PokeRadar -> "PokeRadar"
        HuntMethod.RNGManipulation -> "RNGManipulation"
        HuntMethod.ChainFinishing -> "Chain Fishing"
        HuntMethod.UltraDimension -> "Ultradimension"
        HuntMethod.Other -> "Anderes"
        else -> "???"
    }
}