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
    val stopCode = text("stop_code").nullable()
    val stopName = text("stop_name").nullable()
    val stopDesc = text("stop_desc").nullable()
    val stopLat = decimal("stop_lat", 10, 7).nullable()
    val stopLon = decimal("stop_lon", 10, 7).nullable()
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
    val stopCode by StopTable.stopCode
    val stopName by StopTable.stopName
    val stopDesc by StopTable.stopDesc
    val stopLat by StopTable.stopLat
    val stopLon by StopTable.stopLon
    val zoneId by StopTable.zoneId
    val stopUrl by StopTable.stopUrl
    val locationType by StopTable.locationType
    val parentStation by StopTable.parentStation
    val stopTimezone by StopTable.stopTimezone
    val wheelchairBoarding by StopTable.wheelchairBoarding
    //val levelId by StopTable.levelId
    val platformCode by StopTable.platformCode
}

