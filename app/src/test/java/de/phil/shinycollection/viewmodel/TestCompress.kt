package de.phil.shinycollection.viewmodel

import de.phil.shinycollection.database.GZIPCompression
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