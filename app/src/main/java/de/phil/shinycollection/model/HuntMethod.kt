package de.phil.shinycollection.model

import java.util.*

enum class HuntMethod(val value: Int) {
    Hatch(0),
    SOS(1),
    SoftReset(2),
    FriendSafari(3),
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

        fun fromString(huntMethod: String): HuntMethod {
            return when (huntMethod) {
                "Breeding", "Gezüchtet" -> Hatch
                "SOS-Method", "SOS-Methode" -> SOS
                "Softreset" -> SoftReset
                "Friend Safari", "Kontaktsafari" -> FriendSafari
                "Random", "Zufall" -> Random
                "DexNav" -> DexNav
                "Mass Encounter", "Massenbegegnung" -> Hordes
                "PokeRadar" -> PokeRadar
                "RNGManipulation" -> RNGManipulation
                "Chain Fishing" -> ChainFinishing
                "Ultradimension" -> UltraDimension
                "Other", "Anderes" -> Other
                else -> throw IllegalArgumentException("invalid hunt method: $huntMethod")
            }
        }
    }

}

fun HuntMethod.translateToCurrentLocaleLanguage() = when (Locale.getDefault().language) {
    "de" -> toGerman()
    "en" -> toEnglish()
    else -> toEnglish()
}

private fun HuntMethod.toGerman() = when (this) {
    HuntMethod.Hatch -> "Gezüchtet"
    HuntMethod.SOS -> "SOS-Methode"
    HuntMethod.SoftReset -> "Softreset"
    HuntMethod.FriendSafari -> "Kontaktsafari"
    HuntMethod.Random -> "Zufall"
    HuntMethod.DexNav -> "DexNav"
    HuntMethod.Hordes -> "Massenbegegnung"
    HuntMethod.PokeRadar -> "PokeRadar"
    HuntMethod.RNGManipulation -> "RNGManipulation"
    HuntMethod.ChainFinishing -> "Chain Fishing"
    HuntMethod.UltraDimension -> "Ultradimension"
    HuntMethod.Other -> "Anderes"
}

private fun HuntMethod.toEnglish() = when (this) {
    HuntMethod.Hatch -> "Breeding"
    HuntMethod.SOS -> "SOS-Method"
    HuntMethod.SoftReset -> "Softreset"
    HuntMethod.FriendSafari -> "Friend Safari"
    HuntMethod.Random -> "Random"
    HuntMethod.DexNav -> "DexNav"
    HuntMethod.Hordes -> "Mass Encounter"
    HuntMethod.PokeRadar -> "PokeRadar"
    HuntMethod.RNGManipulation -> "RNGManipulation"
    HuntMethod.ChainFinishing -> "Chain Fishing"
    HuntMethod.UltraDimension -> "Ultradimension"
    HuntMethod.Other -> "Other"
}