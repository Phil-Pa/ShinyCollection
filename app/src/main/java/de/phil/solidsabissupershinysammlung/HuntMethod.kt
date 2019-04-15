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