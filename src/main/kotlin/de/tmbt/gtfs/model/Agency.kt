package de.tmbt.gtfs.model

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object AgencyTable : IdTable<String>() {
    val agencyId = varchar("agency_id", 4096).entityId()

    val name = text("agency_name")
    val url = text("agency_url")
    val timeZone = text("agency_timezone")
    val language = text("agency_lang").nullable()
    val phone = text("agency_phone").nullable()
    val fareUrl = text("agency_fare_url").nullable()
    val email = text("agency_email").nullable()
    override val id: Column<EntityID<String>>
        get() = agencyId
}

class Agency(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Agency>(AgencyTable);

    var name by AgencyTable.name
    var url by AgencyTable.url
    var timeZone by AgencyTable.timeZone
    var lang by AgencyTable.language
    var phone by AgencyTable.phone
    var fareUrl by AgencyTable.fareUrl
    var email by AgencyTable.email
}
