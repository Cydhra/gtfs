package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object FareRuleTable : IdTable<String>() {
    val fare = reference("fare_id", FareAttributeTable).entityId()
    val route = reference("route_id", RouteTable).nullable()
    val origin = text("origin_id").nullable()
    val destination = text("destination_id").nullable()
    val contains = text("contains_id").nullable()

    override val id: Column<EntityID<String>>
        get() = TODO("Not yet implemented")
}