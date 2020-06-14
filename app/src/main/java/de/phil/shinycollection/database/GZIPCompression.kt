package de.phil.shinycollection.database

import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object GZIPCompression {
    fun compress(str: String): String {
        val obj = ByteArrayOutputStream()
        val gzip = GZIPOutputStream(obj)
        gzip.write(str.toByteArray(charset("UTF-8")))
        gzip.close()
        return "GZIP" + Base64.getEncoder().encodeToString(obj.toByteArray())
    }

    fun decompress(str: String): String {
        val test: ByteArray = Base64.getDecoder().decode(str.substring(4))
        val gis =
            GZIPInputStream(
                ByteArrayInputStream(
                    test
                )
            )
        val bf = BufferedReader(
            InputStreamReader(
                gis,
                "UTF-8"
            )
        )
        val sb = java.lang.StringBuilder()
        var line: String?
        while (bf.readLine().also { line = it } != null) {
            sb.append(line)
        }
        gis.close()
        bf.close()
        return sb.toString()
    }

    fun isCompressed(data: String): Boolean {
        return data.startsWith("GZIP")
    }
}