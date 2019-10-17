package de.phil.solidsabissupershinysammlung.database

import androidx.room.TypeConverter
import de.phil.solidsabissupershinysammlung.model.HuntMethod

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
}