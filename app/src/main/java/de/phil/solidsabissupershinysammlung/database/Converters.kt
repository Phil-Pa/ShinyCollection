package de.phil.solidsabissupershinysammlung.database

import androidx.room.TypeConverter
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonEdition

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromHuntMethod(value: Long?): HuntMethod {

        if (value == null) {
            throw Exception()
        }

        return HuntMethod.fromInt(value.toInt())!!
    }

    @TypeConverter
    @JvmStatic
    fun toHuntMethod(method: HuntMethod?): Long? {
        if (method == null) {
            throw Exception()
        }

        return method.ordinal.toLong()
    }

    @TypeConverter
    @JvmStatic
    fun fromPokemonEdition(value: Long?): PokemonEdition {

        if (value == null) {
            throw Exception()
        }

        return PokemonEdition.fromInt(value.toInt())!!
    }

    @TypeConverter
    @JvmStatic
    fun toPokemonEdition(edition: PokemonEdition?): Long? {
        if (edition == null) {
            throw Exception()
        }

        return edition.ordinal.toLong()
    }
}