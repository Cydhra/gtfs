package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or

/**
 * Table representing `attributions.txt`. However, as the GTFS does not specify a primary key an artificial integer key is
 * generated for each entry.
 */
object AttributionTable : IntIdTable() {
    val attributionId = varchar("attribution_id", MAX_IDENTIFIER_LENGTH).nullable()
    val agencyId = reference("agency_id", AgencyTable).nullable()
    val routeId = reference("route_id", RouteTable).nullable()
    val tripId = reference("trip_id", TripTable).nullable()
    val organizationName = varchar("organization_name", MAX_TEXT_LENGTH)
    val isProducer = bool("is_producer").nullable()
    val isOperator = bool("is_operator").nullable()
    val isAuthority = bool("is_authority").nullable()
    val attributionUrl = varchar("attribution_url", MAX_TEXT_LENGTH).nullable()
    val attributionEmail = varchar("attribution_email", MAX_IDENTIFIER_LENGTH).nullable()
    val attributionPhone = varchar("attribution_phone", MAX_IDENTIFIER_LENGTH).nullable()

    init {
        check {
            (agencyId.isNull() and routeId.isNull() and tripId.isNull()) or
                    (agencyId.isNotNull() and routeId.isNull() and tripId.isNull()) or
                    (agencyId.isNull() and routeId.isNotNull() and tripId.isNull()) or
                    (agencyId.isNull() and routeId.isNull() and tripId.isNotNull())
        }
    }
}

class Attribution(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Attribution>(AttributionTable)

    var attributionId by AttributionTable.attributionId
    var agency by Agency optionalReferencedOn AttributionTable.agencyId
    var route by Route optionalReferencedOn AttributionTable.routeId
    var trip by Trip optionalReferencedOn AttributionTable.tripId
    var organizationName by AttributionTable.organizationName
    var isProducer by AttributionTable.isProducer
    var isOperator by AttributionTable.isOperator
    var isAuthority by AttributionTable.isAuthority
    var attributionUrl by AttributionTable.attributionUrl
    var attributionEmail by AttributionTable.attributionEmail
    var attributionPhone by AttributionTable.attributionPhone
}