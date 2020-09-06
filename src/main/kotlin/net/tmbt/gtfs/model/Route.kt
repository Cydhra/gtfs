package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

enum class RouteType {
    TRAM,
    METRO,
    RAIL,
    BUS,
    FERRY,
    CABLE_TRAM,
    AERIAL_LIFT,
    FUNICULAR,
    TROLLEYBUS,
    MONORAIL;

    companion object : ByOrdinal<RouteType>(values())
}

enum class PickupMode {
    //! can board anywhere
    CONTINUOUS,

    //! no continous pickup
    NONE,

    //! must phone agency
    PHONE,

    //! coordinate with driver
    DRIVER;

    companion object : ByOrdinal<PickupMode>(values());
}

object RouteTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("route_id", MAX_IDENTIFIER_LENGTH).entityId()

    val agency = reference("agency_id", AgencyTable).nullable()
    val shortName = varchar("route_short_name", MAX_IDENTIFIER_LENGTH).nullable()
    val longName = varchar("route_long_name", MAX_TEXT_LENGTH).nullable()
    val description = varchar("route_desc", MAX_TEXT_LENGTH).nullable()
    val type = enumeration("route_type", RouteType::class)
    val url = varchar("route_url", MAX_TEXT_LENGTH).nullable()
    val color = varchar("route_color", MAX_IDENTIFIER_LENGTH).nullable()
    val textColor = varchar("route_text_color", MAX_IDENTIFIER_LENGTH).nullable()
    val sortOrder = integer("route_sort_order").nullable()
    val continuousPickup = enumeration("continuous_pickup", PickupMode::class).nullable()
    val continuousDropOff = enumeration("continuous_drop_off", PickupMode::class).nullable()
}

class Route(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Route>(RouteTable)

    var agency by Agency optionalReferencedOn RouteTable.agency
    var shortName by RouteTable.shortName
    var longName by RouteTable.longName
    var description by RouteTable.description
    var type by RouteTable.type
    var url by RouteTable.url
    var color by RouteTable.color
    var textColor by RouteTable.textColor
    var sortOrder by RouteTable.sortOrder
    var continuousPickup by RouteTable.continuousPickup
    var continuousDropOff by RouteTable.continuousDropOff
}
