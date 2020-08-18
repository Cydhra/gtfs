package net.tmbt.gtfs.io

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.logging.log4j.LogManager
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.io.Closeable
import java.io.InputStream

/**
 * Read a GTFS structure from an [InputStream]
 *
 * @param inputStream provides a character stream conforming to GTFS
 *
 * @param T the type of GTFS entity that is produced by this reader implementation
 */
abstract class GtfsReader<out T : Entity<String>>(private val inputStream: InputStream) : Closeable {
    companion object {
        private val logger = LogManager.getLogger()
    }

    private val parser = CSVParser(inputStream.reader(), CSVFormat.RFC4180)
    private val recordIterator = parser.iterator()
    private val gtfsHeader = recordIterator.next()

    /**
     * Read the next available entity from the provided [inputStream]
     */
    fun nextEntity(): T {
        val record = this.recordIterator.next()
        return this.createEntity(gtfsHeader.map { column -> column to record[column] }.toMap())
    }

    /**
     * Read all entities from the provided [inputStream] until reaching end of file.
     *
     * @return a list of all read entities
     */
    fun readRemainingEntities(): List<T> {
        assert(TransactionManager.currentOrNull() != null)

        val results = mutableListOf<T>()
        while (recordIterator.hasNext()) {
            results += this.nextEntity()
        }

        return results
    }

    /**
     * Create an instance of [T] and insert it into the database by mapping the values provided in [entries] into the
     * entity
     *
     * @param entries a key-value store providing the column entries for a [T] entity
     */
    protected abstract fun createEntity(entries: Map<String, String>): T

    override fun close() {
        this.inputStream.close()
        this.parser.close()
    }
}