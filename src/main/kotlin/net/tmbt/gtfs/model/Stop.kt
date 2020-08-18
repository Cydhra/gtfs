package net.tmbt.gtfs.model

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
    val platform_code = text("platform_code").nullable()

    override val id: Column<EntityID<String>>
        get() = TODO("Not yet implemented")
}

