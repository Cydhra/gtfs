package net.tmbt.gtfs.model

import net.tmbt.gtfs.model.RouteTable.entityId
import net.tmbt.gtfs.model.RouteTable.nullable
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
    MONORAIL
}

enum class PickupMode {
    //! can board anywhere
    CONTINOUS,

    //! no continous pickup
    NONE,

    //! must phone agency
    PHONE,

    //! coordinate with driver
    DRIVER
}

object RouteTable : IdTable<String>() {
    val routeId = text("route_id").entityId()
    val agency = reference("agency_id", AgencyTable).nullable()
    val shortName = text("route_short_name").nullable()
    val longName = text("route_long_name").nullable()
    val description = text("route_desc").nullable()
    val type = enumeration("route_type", RouteType::class)
    val url = text("route_url").nullable()
    val color = text("route_color").nullable()
    val textColor = text("route_text_color").nullable()
    val sortOrder = integer("route_sort_order").nullable()
    val continousPickup = enumeration("continous_pickup", PickupMode::class).nullable()
    val continousDropOff = enumeration("continous_drop_off", PickupMode::class).nullable()

    override val id: Column<EntityID<String>>
        get() = routeId
}

class Route(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Route>(RouteTable)

    val routeId by RouteTable.routeId
    val agency by Agency optionalBackReferencedOn RouteTable.agency
    val shortName by RouteTable.shortName
    val longName by RouteTable.longName
    val description by RouteTable.description
    val type by RouteTable.type
    val url by RouteTable.url
    val color by RouteTable.color
    val textColor by RouteTable.textColor
    val sortOrder by RouteTable.sortOrder
    val continousPickup by RouteTable.continousPickup
    val continousDropOff by RouteTable.continousDropOff
}
