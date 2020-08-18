package net.tmbt.gtfs.io

import java.io.Closeable
import java.io.InputStream

/**
 * Read a GTFS structure from an [InputStream]
 *
 * @param inputStream provides a character stream conforming to GTFS
 */
class GtfsReader(private val inputStream: InputStream) : Closeable {

    override fun close() {
        this.inputStream.close()
    }
}