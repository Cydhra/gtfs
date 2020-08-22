package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.FrequencyTable
import net.tmbt.gtfs.model.TripTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsFrequenciesReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(entries["trip_id"] ?: error("cannot create frequency without trip id"), TripTable)

            FrequencyTable.insert { row ->
                row[startTime] = entries["start_time"] ?: error("cannot create frequency without start_time")
                row[endTime] = entries["end_time"] ?: error("cannot create frequency without end_time")
                row[headway] = entries["headway_secs"]?.toInt() ?: error("cannot create frequency without headway_secs")
                row[exact] = entries["exact_times"]?.toInt() == 1
                row[id] = entityId
            }

            return@transaction entityId
        }
    }
}