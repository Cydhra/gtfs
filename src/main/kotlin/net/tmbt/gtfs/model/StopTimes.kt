package net.tmbt.gtfs.model

import net.tmbt.gtfs.model.StopTimeTable.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import javax.swing.DropMode

enum class TimeMode {
    APPROXIMATE,
    EXACT
}

object StopTimeTable : IntIdTable() {
    val trip = reference("trip_id", TripTable)
    val stop = reference("stop_id", StopTable)
    val arrivalTime = text("arrival_time").nullable()
    val departureTime = text("departure_time").nullable()
    val stopSequence = integer("stop_sequence")
    val headsign = text("stop_headsign").nullable()
    val pickupType = enumeration("pickup_type", PickupMode::class).nullable()
    val dropOffType = enumeration("drop_off_type", PickupMode::class).nullable()
    val continousPickup = enumeration("continous_pickup", PickupMode::class).nullable()
    val continous = enumeration("continous_drop_off", PickupMode::class).nullable()
    val shapeDist = float("shape_dist_traveled").nullable()
    val timepoint = enumeration("timepoint", TimeMode::class).nullable()

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
    var headsign by StopTimeTable.headsign
    var pickupType by StopTimeTable.pickupType
    var dropOffType by StopTimeTable.dropOffType
    var continousPickup by StopTimeTable.continousPickup
    var continous by StopTimeTable.continous
    var shapeDist by StopTimeTable.shapeDist
    var timepoint by StopTimeTable.timepoint
}

