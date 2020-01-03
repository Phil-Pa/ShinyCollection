package de.phil.solidsabissupershinysammlung.database

import de.phil.android.lib.encoding.Base64StringCompression
import de.phil.android.lib.encoding.shannon.ShannonAlgorithm
import de.phil.android.lib.math.NativeNumberConverter

class DataManager {

    private val dataImporter = DataImporter()
    private val dataExporter = DataExporter()

    private val map = mapOf(
        '0' to "00000",
        '1' to "00001",
        '2' to "0001",
        '3' to "001",
        '4' to "01000",
        '5' to "01001",
        '6' to "0101",
        '7' to "011",
        '8' to "10000",
        '9' to "10001",
        '(' to "1001",
        ')' to "101",
        ',' to "11000",
        ' ' to "11001",
        '\n' to "1101"
    )

    private val shannonAlgorithm = ShannonAlgorithm(map)
    private val nativeNumberConverter = NativeNumberConverter()
    private val compressor = Base64StringCompression(shannonAlgorithm, nativeNumberConverter)

    fun import(pokemonRepository: IPokemonRepository, data: String?): Boolean {

        if (data == null)
            return false

        val isCompressed = !data.contains(" ")
        return if (isCompressed) {
            val str = compressor.decompress(data)
            dataImporter.import(pokemonRepository, str)
        } else {
            dataImporter.import(pokemonRepository, data)
        }
    }

    fun export(shouldCompressData: Boolean, pokemonRepository: IPokemonRepository): String? {

        dataExporter.shouldCompressData = shouldCompressData

        if (shouldCompressData) {
            val data = dataExporter.export(pokemonRepository) ?: return null
            return compressor.compress(data)
        } else {
            return dataExporter.export(pokemonRepository)
        }
    }

}