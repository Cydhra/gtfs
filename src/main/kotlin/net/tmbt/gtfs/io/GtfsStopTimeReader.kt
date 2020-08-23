package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsStopTimeReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            val entityId = InsertStatement<Number>(StopTimeTable).apply {
                this[StopTimeTable.trip] = entries["trip_id"]?.let { EntityID(it, TripTable) }
                    ?: error("cannot create stop time without trip_id")
                this[StopTimeTable.stop] = entries["stop_id"]?.let { EntityID(it, StopTable) }
                    ?: error("cannot create stop time without stop_id")
                this[StopTimeTable.arrivalTime] = entries["arrival_time"]
                this[StopTimeTable.departureTime] = entries["departure_time"]
                this[StopTimeTable.stopSequence] =
                    entries["stop_sequence"]?.toInt() ?: error("cannot create stop time without stop_sequence")
                this[StopTimeTable.headSign] = entries["stop_headsign"]
                this[StopTimeTable.pickupType] = PickupMode.byOrdinalOrNull(entries["pickup_type"]?.toInt())
                this[StopTimeTable.dropOffType] = PickupMode.byOrdinalOrNull(entries["drop_off_type"]?.toInt())
                this[StopTimeTable.continuousPickup] = PickupMode.byOrdinalOrNull(entries["continuous_pickup"]?.toInt())
                this[StopTimeTable.continuous] = PickupMode.byOrdinalOrNull(entries["continuous_drop_off"]?.toInt())
                this[StopTimeTable.shapeDist] = entries["shape_dist_traveled"]?.toFloat()
                this[StopTimeTable.timePoint] = TimeMode.byOrdinalOrNull(entries["timepoint"]?.toInt())
            }.execute(TransactionManager.current())!!

            return@transaction EntityID(entityId, StopTimeTable)
        }
    }
}