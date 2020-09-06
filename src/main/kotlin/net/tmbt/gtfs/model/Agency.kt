package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object AgencyTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("agency_id", MAX_IDENTIFIER_LENGTH).entityId()

    val name = varchar("agency_name", MAX_TEXT_LENGTH)
    val url = varchar("agency_url", MAX_TEXT_LENGTH)
    val timeZone = varchar("agency_timezone", MAX_IDENTIFIER_LENGTH)
    val language = varchar("agency_lang", MAX_IDENTIFIER_LENGTH).nullable()
    val phone = varchar("agency_phone", MAX_IDENTIFIER_LENGTH).nullable()
    val fareUrl = varchar("agency_fare_url", MAX_TEXT_LENGTH).nullable()
    val email = varchar("agency_email", MAX_IDENTIFIER_LENGTH).nullable()
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
