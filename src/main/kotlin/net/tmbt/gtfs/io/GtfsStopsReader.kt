package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.Availability
import net.tmbt.gtfs.model.LocationType
import net.tmbt.gtfs.model.StopTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsStopsReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(
                entries["stop_id"] ?: error("cannot create stop without id"),
                StopTable
            )

            StopTable.insert { row ->
                row[stopId] = entityId
                row[code] = entries["stop_code"]
                row[name] = entries["stop_name"]
                row[desc] = entries["stop_desc"]
                row[lat] = entries["stop_lat"]?.toBigDecimal()
                row[lon] = entries["stop_lon"]?.toBigDecimal()
                row[zoneId] = entries["zone_id"]
                row[stopUrl] = entries["stop_url"]
                row[locationType] = LocationType.byOrdinalOrNull(entries["location_type"]?.toInt())
                row[parentStation] = entries["parent_station"]?.let { EntityID(it, StopTable) }
                row[stopTimezone] = entries["stop_timezone"]
                row[wheelchairBoarding] = Availability.byOrdinalOrNull(entries["wheelchair_boarding"]?.toInt())
                //row[StopTable.levelId] = entries[""]
                row[platformCode] = entries["platform_code"]
            }

            return@transaction entityId
        }
    }
}