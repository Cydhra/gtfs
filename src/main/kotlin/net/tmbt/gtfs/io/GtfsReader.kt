package net.tmbt.gtfs.io

import net.tmbt.gtfs.util.UnicodeBOMInputStream
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.io.Closeable
import java.io.InputStream

/**
 * Read a GTFS structure from an [InputStream].
 * The specific sub-classes all read one type of file into the thread-local database.
 * See [the GTFS reference](https://developers.google.com/transit/gtfs/reference#dataset_files) for details.
 *
 * @param inputStream provides a character stream conforming to GTFS
 *
 * @param ID the data type of the identifier for this specific GTFS record. Usually [String].
 */
abstract class GtfsReader<ID : Comparable<ID>>(private val inputStream: InputStream) : Closeable {
    private val parser =
        CSVParser(UnicodeBOMInputStream(inputStream).skipBOM().reader(), CSVFormat.RFC4180.withHeader())
    private val recordIterator = parser.iterator()
    private val gtfsHeader: List<String>

    init {
        gtfsHeader = parser.headerMap.keys.toList()
    }

    /**
     * Read the next available entity from the provided [inputStream] and dump it into the thread-local database.
     *
     * @return the database id of the entity (as specified by GTFS)
     */
    fun nextEntity(): EntityID<ID> {
        assert(TransactionManager.currentOrNull() != null)

        val record = this.recordIterator.next()
        return this.insertEntity(gtfsHeader
            .mapNotNull { column -> (column to record[column]?.takeIf { it.isNotEmpty() }).takeIf { it.second != null } }
            .map { it.first to it.second!! }
            .toMap())
    }

    /**
     * Read all entities from the provided [inputStream] until reaching end of file.
     * All read entities will be dumped into the thread-local database.
     *
     * @return a list of all read entity ids
     */
    fun readRemainingEntities(): List<EntityID<ID>> {
        assert(TransactionManager.currentOrNull() != null)

        val results = mutableListOf<EntityID<ID>>()
        while (recordIterator.hasNext()) {
            results += this.nextEntity()
        }

        return results
    }

    /**
     * Insert a new instance of a GTFS entity into the database.
     * Use the provided map to access the GTFS columns and map them to the specific SQL columns.
     *
     * @param entries a key-value store providing the column entries for the entity
     */
    protected abstract fun insertEntity(entries: Map<String, String>): EntityID<ID>

    override fun close() {
        this.inputStream.close()
        this.parser.close()
    }
}