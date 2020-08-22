package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.AgencyTable
import net.tmbt.gtfs.model.PickupMode
import net.tmbt.gtfs.model.RouteTable
import net.tmbt.gtfs.model.RouteType
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsRoutesReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(entries["route_id"] ?: error("cannot create route without route_id"), RouteTable)

            RouteTable.insert { row ->
                row[id] = entityId
                row[agency] = entries["agency_id"]?.let { EntityID(it, AgencyTable) }
                row[shortName] = entries["route_short_name"]
                row[longName] = entries["route_long_name"]
                row[description] = entries["route_desc"]
                row[type] = RouteType.byOrdinalOrNull(entries["route_type"]?.toInt())
                    ?: error("cannot create route without type")
                row[url] = entries["route_url"]
                row[color] = entries["route_color"]
                row[textColor] = entries["route_text_color"]
                row[sortOrder] = entries["route_sort_order"]?.toInt()
                row[continuousPickup] = PickupMode.byOrdinalOrNull(entries["continuous_pickup"]?.toInt())
                row[continuousDropOff] = PickupMode.byOrdinalOrNull(entries["continuous_drop_off"]?.toInt())
            }

            return@transaction entityId
        }
    }
}