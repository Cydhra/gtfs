package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object AttributionTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("attribution_id", MAX_IDENTIFIER_LENGTH).entityId()

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
}

class Attribution(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Attribution>(AttributionTable)

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