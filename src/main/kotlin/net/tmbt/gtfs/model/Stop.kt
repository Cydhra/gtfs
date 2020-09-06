package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

enum class LocationType {
    STOP,
    STATION,
    ENTRANCE,
    GENERIC,
    BOARDING_AREA;

    companion object : ByOrdinal<LocationType>(values())
}

enum class Availability {
    UNKNOWN,
    AVAILABLE,
    UNAVAILABLE;

    companion object : ByOrdinal<Availability>(values())
}

object StopTable : IdTable<String>() {
    override val id = varchar("stop_id", MAX_IDENTIFIER_LENGTH).entityId()

    val code = varchar("stop_code", MAX_IDENTIFIER_LENGTH).nullable()
    val name = varchar("stop_name", MAX_TEXT_LENGTH).nullable()
    val desc = varchar("stop_desc", MAX_TEXT_LENGTH).nullable()
    val lat = decimal("stop_lat", 10, 7).nullable()
    val lon = decimal("stop_lon", 10, 7).nullable()
    val zoneId = varchar("zone_id", MAX_IDENTIFIER_LENGTH).nullable()
    val stopUrl = varchar("stop_url", MAX_TEXT_LENGTH).nullable()
    val locationType = enumeration("location_type", LocationType::class).nullable()
    val parentStation = reference("parent_station", StopTable).nullable()
    val stopTimezone = varchar("stop_timezone", MAX_IDENTIFIER_LENGTH).nullable()
    val wheelchairBoarding = enumeration("wheelchair_boarding", Availability::class).nullable()
    val levelId = reference("level_id", LevelTable).nullable()
    val platformCode = varchar("platform_code", MAX_IDENTIFIER_LENGTH).nullable()
}

class Stop(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Stop>(StopTable)

    var code by StopTable.code
    var name by StopTable.name
    var desc by StopTable.desc
    var lat by StopTable.lat
    var lon by StopTable.lon
    var zoneId by StopTable.zoneId
    var stopUrl by StopTable.stopUrl
    var locationType by StopTable.locationType
    var parentStation by Stop optionalReferencedOn StopTable.parentStation
    var stopTimezone by StopTable.stopTimezone
    var wheelchairBoarding by StopTable.wheelchairBoarding
    var levelId by Level optionalReferencedOn StopTable.levelId
    var platformCode by StopTable.platformCode
}

