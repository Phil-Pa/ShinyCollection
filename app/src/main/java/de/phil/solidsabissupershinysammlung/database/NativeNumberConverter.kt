package de.phil.solidsabissupershinysammlung.database

import de.phil.android.lib.math.NumberConverter

class NativeNumberConverter : NumberConverter {

    override fun convert(baseFrom: Int, baseTo: Int, number: String): String {
        return convertNumber(baseFrom, baseTo, number)
    }

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    private external fun convertNumber(fromBase: Int, toBase: Int, number: String?): String

}