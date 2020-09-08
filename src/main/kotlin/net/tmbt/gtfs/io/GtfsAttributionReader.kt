package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.AgencyTable
import net.tmbt.gtfs.model.AttributionTable
import net.tmbt.gtfs.model.RouteTable
import net.tmbt.gtfs.model.TripTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsAttributionReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {

    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(
                entries["attribution_id"] ?: error("cannot create attribution without id"),
                AgencyTable
            )

            AttributionTable.insert { row ->
                row[id] = entityId

                row[agencyId] = entries["agency_id"]?.let { EntityID(it, AgencyTable) }
                row[routeId] = entries["route_id"]?.let { EntityID(it, RouteTable) }
                row[tripId] = entries["trip_id"]?.let { EntityID(it, TripTable) }
                row[organizationName] =
                    entries["organization_name"] ?: error("cannot create attribution without organization_name")
                row[isProducer] = entries[""]?.let { it == "1" }
                row[isOperator] = entries[""]?.let { it == "1" }
                row[isAuthority] = entries[""]?.let { it == "1" }
                row[attributionUrl] = entries["attribution_url"]
                row[attributionEmail] = entries["attribution_email"]
                row[attributionPhone] = entries["attribution_phone"]
            }

            return@transaction entityId
        }
    }
}