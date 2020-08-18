package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

enum class LocationType {
    STOP,
    STATION,
    ENTRANCE,
    GENERIC,
    BOARDING_AREA
}

enum class WheelchairBoarding {
    UNKNOWN,
    POSSIBLE,
    NOT_POSSIBLE
}

object StopTable : IdTable<String>() {
    val stopId = text("stop_id").entityId()
    val code = text("stop_code").nullable()
    val name = text("stop_name").nullable()
    val desc = text("stop_desc").nullable()
    val lat = decimal("stop_lat", 10, 7).nullable()
    val lon = decimal("stop_lon", 10, 7).nullable()
    val zoneId = text("zone_id").nullable()
    val stopUrl = text("stop_url").nullable()
    val locationType = enumeration("location_type", LocationType::class).nullable()
    val parentStation = reference("parent_station", StopTable).nullable()
    val stopTimezone = text("stop_timezone").nullable()
    val wheelchairBoarding = enumeration("wheelchair_boarding", WheelchairBoarding::class).nullable()
    //val levelId = reference("level_id", TODO level table).nullable()
    val platformCode = text("platform_code").nullable()

    override val id: Column<EntityID<String>>
        get() = TODO("Not yet implemented")
}

class Stop(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Agency>(AgencyTable)

    val stopId by StopTable.stopId
    val code by StopTable.code
    val name by StopTable.name
    val desc by StopTable.desc
    val lat by StopTable.lat
    val lon by StopTable.lon
    val zoneId by StopTable.zoneId
    val stopUrl by StopTable.stopUrl
    val locationType by StopTable.locationType
    val parentStation by Stop optionalReferencedOn StopTable.parentStation
    val stopTimezone by StopTable.stopTimezone
    val wheelchairBoarding by StopTable.wheelchairBoarding
    //val levelId by StopTable.levelId
    val platformCode by StopTable.platformCode
}

