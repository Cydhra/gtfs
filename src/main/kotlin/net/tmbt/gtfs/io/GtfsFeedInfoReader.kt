package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.FeedInfoTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsFeedInfoReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            FeedInfoTable.insertAndGetId { row ->
                row[feedPublisherName] = entries["feed_publisher_name"]
                    ?: error("cannot create feed_info without feed_publisher_name")
                row[feedPublisherUrl] = entries["feed_publisher_url"]
                    ?: error("cannot create feed_info without feed_publisher_url")
                row[feedLanguage] = entries["feed_lang"]
                    ?: error("cannot create feed_info without feed_lang")
                row[defaultLanguage] = entries["default_lang"]
                row[feedStartDate] = entries["feed_start_date"]
                row[feedEndDate] = entries["feed_end_date"]
                row[feedVersion] = entries["feed_version"]
                row[feedContactEmail] = entries["feed_contact_email"]
                row[feedContactUrl] = entries["feed_contact_url"]
            }
        }
    }
}