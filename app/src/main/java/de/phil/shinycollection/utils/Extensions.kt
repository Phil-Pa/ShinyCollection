package de.phil.shinycollection.utils

import de.phil.shinycollection.BuildConfig

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return (kotlin.math.round(this * multiplier) / multiplier)
}

private val sb = StringBuilder(10)

fun prependZerosToNumber(numberLength: Int, number: Int): String {
    sb.clear()
    sb.append(number)

    val digits = sb.length

    sb.clear()

    val zerosLeft = numberLength - digits
    if (BuildConfig.DEBUG && zerosLeft < 0) {
        error("Assertion failed")
    }

    sb.clear()

    for (i in 0 until zerosLeft)
        sb.append('0')

    sb.append(number)
    return sb.toString()
}

enum class MessageType {
    Success,
    Warning,
    Info,
    Error
}