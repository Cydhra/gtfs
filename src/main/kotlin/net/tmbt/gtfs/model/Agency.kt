package net.tmbt.gtfs.model

import net.tmbt.gtfs.model.AgencyTable.nullable
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

object AgencyTable : IdTable<String>() {
    val agencyId = varchar("agency_id", 4096).entityId()

    val agencyName = text("agency_name")
    val agencyUrl = text("agency_url")
    val timeZone = text("agency_timezone")
    val agencyLang = text("agency_lang").nullable()
    val agencyPhone = text("agency_phone").nullable()
    val agencyFareUrl = text("agency_fare_url").nullable()
    val agencyEmail = text("agency_email").nullable()
    override val id: Column<EntityID<String>>
        get() = agencyId
}

class Agency(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Agency>(AgencyTable);

    var agencyName by AgencyTable.agencyName
    var agencyUrl by AgencyTable.agencyUrl
    var timeZone by AgencyTable.timeZone
    var agencyLang by AgencyTable.agencyLang
    var agencyPhone by AgencyTable.agencyPhone
    var agencyFareUrl by AgencyTable.agencyFareUrl
    var agencyEmail by AgencyTable.agencyEmail
}
