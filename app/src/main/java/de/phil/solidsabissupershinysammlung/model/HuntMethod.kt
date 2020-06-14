package de.phil.solidsabissupershinysammlung.model

enum class HuntMethod(val value: Int) {
    Hatch(0),
    SOS(1),
    FriendSafari(2),
    SoftReset(3),
    Random(4),
    DexNav(5),
    Hordes(6),
    PokeRadar(7),
    RNGManipulation(8),
    ChainFinishing(9),
    UltraDimension(10),
    Other(11);

    companion object {
        private val map = values().associateBy(HuntMethod::ordinal)
        fun fromInt(type: Int) = map[type]
    }

}

fun HuntMethod.translateToLanguage(language: String): String {
    return when (language) {
        "de" -> toGerman()
        "en" -> toEnglish()
        else -> toEnglish()
    }
}

private fun HuntMethod.toGerman(): String {
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
    }
}

private fun HuntMethod.toEnglish(): String {
    return when (this) {
        HuntMethod.Hatch -> "Breeded"
        HuntMethod.SOS -> "SOS-Method"
        HuntMethod.FriendSafari -> "Friend Safari"
        HuntMethod.SoftReset -> "Softreset"
        HuntMethod.Random -> "Random"
        HuntMethod.DexNav -> "DexNav"
        HuntMethod.Hordes -> "Mass Encounter"
        HuntMethod.PokeRadar -> "PokeRadar"
        HuntMethod.RNGManipulation -> "RNGManipulation"
        HuntMethod.ChainFinishing -> "Chain Fishing"
        HuntMethod.UltraDimension -> "Ultradimension"
        HuntMethod.Other -> "Other"
    }
}