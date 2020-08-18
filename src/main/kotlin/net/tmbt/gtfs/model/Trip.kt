package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

enum class TripDirection {
    OUTBOUND,
    INBOUND;

    companion object : ByOrdinal<TripDirection>(values())
}

object TripTable : IdTable<String>() {
    val route = reference("route_id", RouteTable)

    //val service = reference("service_id", CalendarDateTable) TODO: two tables?!
    val trip = text("trip_id")
    val headsign = text("trip_headsign").nullable()
    val shortName = text("trip_short_name").nullable()
    val direction = enumeration("direction_id", TripDirection::class).nullable()
    val block = text("block_id").nullable()
    val shape = reference("shape_id", ShapeTable).nullable()
    val wheelchair = enumeration("wheelchair_accessible", Availability::class).nullable()
    val bikesAllowed = enumeration("bikes_allowed", Availability::class).nullable()


    override val id: Column<EntityID<String>> = text("trip_id").entityId()

}

class Trip(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Trip>(TripTable)

    var route by TripTable.route

    //var service  by TripTable.service
    var trip by TripTable.id
    var headsign by TripTable.headsign
    var shortName by TripTable.shortName
    var direction by TripTable.direction
    var block by TripTable.block
    var shape by TripTable.shape
    var wheelchair by TripTable.wheelchair
    var bikesAllowed by TripTable.bikesAllowed
}
