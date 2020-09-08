package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsTripReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(entries["trip_id"] ?: error("cannot create trip without trip id"), RouteTable)

            TripTable.insert { row ->
                row[id] = entityId

                row[route] = EntityID(entries["route_id"] ?: error("cannot create trip without route id"), RouteTable)

                // request service id in both relevant tables and then reference the matching table
                val serviceId = entries["service_id"] ?: error("cannot create trip without service id")
                val calendarEntity = CalendarTable.select { CalendarTable.id eq serviceId }.firstOrNull()
                if (calendarEntity == null) {
                    val calendarDateEntity =
                        CalendarDateTable.select { CalendarDateTable.id eq serviceId }.firstOrNull()
                            ?: throw IllegalStateException("cannot find Calendar or CalendarDate with id \"$serviceId\"")

                    row[serviceCalendarDate] = calendarDateEntity[CalendarDateTable.id]
                } else {
                    row[serviceCalendar] = calendarEntity[CalendarTable.id]
                }

                row[headsign] = entries["trip_headsign"]
                row[shortName] = entries["trip_short_name"]
                row[direction] = TripDirection.byOrdinalOrNull(entries["direction_id"]?.toInt())
                row[block] = entries["block_id"]
                row[shape] = entries["shape_id"]?.let { EntityID(it, ShapeTable) }
                row[wheelchair] = Availability.byOrdinalOrNull(entries["wheelchair_accessible"]?.toInt())
                row[bikesAllowed] = Availability.byOrdinalOrNull(entries["bikes_allowed"]?.toInt())
            }

            return@transaction entityId
        }
    }
}