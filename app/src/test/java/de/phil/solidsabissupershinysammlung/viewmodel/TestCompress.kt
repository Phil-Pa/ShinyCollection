package de.phil.solidsabissupershinysammlung.viewmodel

import de.phil.solidsabissupershinysammlung.database.GZIPCompression
import org.junit.Test

class TestCompress {

    @Test
    fun testCompress() {

        val str = TestData.CLIPBOARD_DATA

        val compressed = GZIPCompression.compress(str)
        val decompressed = GZIPCompression.decompress(compressed)

        assert(GZIPCompression.isCompressed(compressed))
        assert(!GZIPCompression.isCompressed(decompressed))
        assert(compressed.length < str.length)
        assert(str == decompressed)
    }

}