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
    BOARDING_AREA;

    companion object {
        fun byOrdinalOrNull(ordinal: Int?): LocationType? {
            return if (ordinal == null)
                null
            else
                values()[ordinal]
        }
    }
}

enum class Availability {
    UNKNOWN,
    AVAILABLE,
    UNAVAILABLE
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
    val wheelchairBoarding = enumeration("wheelchair_boarding", Availability::class).nullable()
    //val levelId = reference("level_id", TODO level table).nullable()
    val platformCode = text("platform_code").nullable()

    override val id: Column<EntityID<String>>
        get() = TODO("Not yet implemented")
}

class Stop(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Stop>(StopTable)

    var stopId by StopTable.stopId
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

    //var levelId by StopTable.levelId
    var platformCode by StopTable.platformCode
}

