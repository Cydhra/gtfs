package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.Availability
import net.tmbt.gtfs.model.LevelTable
import net.tmbt.gtfs.model.LocationType
import net.tmbt.gtfs.model.StopTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.io.InputStream

class GtfsStopsReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(
                entries["stop_id"] ?: error("cannot create stop without id"),
                StopTable
            )

            StopTable.insert { row ->
                row[this.id] = entityId
                row[code] = entries["stop_code"]
                row[name] = entries["stop_name"]
                row[desc] = entries["stop_desc"]
                row[lat] = entries["stop_lat"]?.toBigDecimal()
                row[lon] = entries["stop_lon"]?.toBigDecimal()
                row[zoneId] = entries["zone_id"]
                row[stopUrl] = entries["stop_url"]
                row[locationType] = LocationType.byOrdinalOrNull(entries["location_type"]?.toInt())

                // reference the parent station if it exists or create a weak reference to fix later
                val parentStationId = entries["parent_station"]
                if (parentStationId != null) {
                    val parentStationEntity = StopTable.select { parentStation eq parentStationId }.firstOrNull()
                    if (parentStationEntity != null)
                        row[parentStation] = parentStationEntity[id]
                    else
                        row[weakParent] = parentStationId
                }

                row[stopTimezone] = entries["stop_timezone"]
                row[wheelchairBoarding] = Availability.byOrdinalOrNull(entries["wheelchair_boarding"]?.toInt())
                row[levelId] = entries["level_id"]?.let { EntityID(it, LevelTable) }
                row[platformCode] = entries["platform_code"]
            }

            // update weak keys referencing this new entity with a reference to it
            StopTable.update(where = { StopTable.weakParent eq entries["stop_id"] }) { row ->
                row[parentStation] = entityId
                row[weakParent] = null
            }

            return@transaction entityId
        }
    }
}