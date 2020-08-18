package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.Agency
import net.tmbt.gtfs.model.AgencyTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

/**
 * Reader for agencies.txt of the GTFS specification.
 *
 * @param inputStream an [InputStream] that contains a GTFS CSV file
 */
class GtfsAgencyReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {

    /**
     * Create an [Agency] in the current database from the values provided in the map
     */
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(
                entries["agency_id"] ?: error("cannot create agency without id"),
                AgencyTable
            )

            AgencyTable.insert {
                it[id] = entityId
                it[name] = entries["agency_name"] ?: error("cannot create agency without name")
                it[timeZone] = entries["agency_timezone"] ?: error("cannot create agency without timezone")
                it[url] = entries["agency_url"] ?: error("cannot create agency without url")
                it[language] = entries["agency_lang"]
                it[phone] = entries["agency_phone"]
                it[fareUrl] = entries["agency_fare_url"]
                it[email] = entries["agency_email"]
            }

            return@transaction entityId
        }
    }
}