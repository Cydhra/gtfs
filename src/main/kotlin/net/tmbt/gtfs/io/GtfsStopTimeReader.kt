package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsStopTimeReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            StopTimeTable.insertAndGetId { row ->
                row[trip] = entries["trip_id"]?.let { EntityID(it, TripTable) }
                    ?: error("cannot create stop time without trip_id")
                row[stop] = entries["stop_id"]?.let { EntityID(it, StopTable) }
                    ?: error("cannot create stop time without stop_id")
                row[arrivalTime] = entries["arrival_time"]
                row[departureTime] = entries["departure_time"]
                row[stopSequence] =
                    entries["stop_sequence"]?.toInt() ?: error("cannot create stop time without stop_sequence")
                row[headSign] = entries["stop_headsign"]
                row[pickupType] = PickupMode.byOrdinalOrNull(entries["pickup_type"]?.toInt())
                row[dropOffType] = PickupMode.byOrdinalOrNull(entries["drop_off_type"]?.toInt())
                row[continuousPickup] = PickupMode.byOrdinalOrNull(entries["continuous_pickup"]?.toInt())
                row[continuous] = PickupMode.byOrdinalOrNull(entries["continuous_drop_off"]?.toInt())
                row[shapeDist] = entries["shape_dist_traveled"]?.toFloat()
                row[timePoint] = TimeMode.byOrdinalOrNull(entries["timepoint"]?.toInt())
            }
        }
    }
}