package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object FareRuleTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = reference("fare_id", FareAttributeTable)

    val route = reference("route_id", RouteTable).nullable()
    val origin = varchar("origin_id", MAX_IDENTIFIER_LENGTH).nullable()
    val destination = varchar("destination_id", MAX_IDENTIFIER_LENGTH).nullable()
    val contains = varchar("contains_id", MAX_IDENTIFIER_LENGTH).nullable()
}