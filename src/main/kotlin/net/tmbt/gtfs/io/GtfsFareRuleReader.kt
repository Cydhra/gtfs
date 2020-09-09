package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.FareAttributeTable
import net.tmbt.gtfs.model.FareRuleTable
import net.tmbt.gtfs.model.RouteTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsFareRuleReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {

    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            FareRuleTable.insertAndGetId { row ->
                row[fareId] =
                    EntityID(entries["fare_id"] ?: error("cannot create fare rule without fare id"), FareAttributeTable)

                row[route] = entries["route_id"]?.let { EntityID(it, RouteTable) }
                row[origin] = entries["origin"]
                row[destination] = entries["destination"]
                row[contains] = entries["contains"]
            }
        }
    }
}