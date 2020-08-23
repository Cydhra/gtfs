package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

enum class TimeMode {
    APPROXIMATE,
    EXACT;

    companion object : ByOrdinal<TimeMode>(values())
}

object StopTimeTable : IntIdTable() {
    val trip = reference("trip_id", TripTable)
    val stop = reference("stop_id", StopTable)
    val arrivalTime = text("arrival_time").nullable()
    val departureTime = text("departure_time").nullable()
    val stopSequence = integer("stop_sequence")
    val headSign = text("stop_headsign").nullable()
    val pickupType = enumeration("pickup_type", PickupMode::class).nullable()
    val dropOffType = enumeration("drop_off_type", PickupMode::class).nullable()
    val continuousPickup = enumeration("continuous_pickup", PickupMode::class).nullable()
    val continuous = enumeration("continuous_drop_off", PickupMode::class).nullable()
    val shapeDist = float("shape_dist_traveled").nullable()
    val timePoint = enumeration("timepoint", TimeMode::class).nullable()

    init {
        uniqueIndex(trip, stop)
    }
}

class StopTime(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StopTime>(StopTimeTable)

    var trip by StopTimeTable.trip
    var stop by StopTimeTable.stop
    var arrivalTime by StopTimeTable.arrivalTime
    var departureTime by StopTimeTable.departureTime
    var stopSequence by StopTimeTable.stopSequence
    var headSign by StopTimeTable.headSign
    var pickupType by StopTimeTable.pickupType
    var dropOffType by StopTimeTable.dropOffType
    var continuousPickup by StopTimeTable.continuousPickup
    var continous by StopTimeTable.continuous
    var shapeDist by StopTimeTable.shapeDist
    var timePoint by StopTimeTable.timePoint
}

