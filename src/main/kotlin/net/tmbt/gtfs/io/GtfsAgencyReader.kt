package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.Agency
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

/**
 * Reader for agencies.txt of the GTFS specification.
 *
 * @param inputStream an [InputStream] that contains a GTFS CSV file
 */
class GtfsAgencyReader(inputStream: InputStream) : GtfsReader<Agency>(inputStream) {

    /**
     * Create an [Agency] in the current database from the values provided in the map
     */
    override fun createEntity(entries: Map<String, String>): Agency {
        return transaction {
            Agency.new {
                this.name = entries["agency_name"] ?: error("cannot create agency without name")
                this.timeZone = entries["agency_timezone"] ?: error("cannot create agency without timezone")
                this.url = entries["agency_url"] ?: error("cannot create agency without url")
                this.lang = entries["agency_lang"]
                this.phone = entries["agency_phone"]
                this.fareUrl = entries["agency_fare_url"]
                this.email = entries["agency_email"]
            }
        }
    }
}