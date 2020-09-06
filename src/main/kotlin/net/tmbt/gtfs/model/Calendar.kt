package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

enum class DailyAvailability {
    AVAILABLE,
    UNAVAILABLE;

    companion object : ByOrdinal<DailyAvailability>(values())
}

object CalendarTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("service_id", MAX_IDENTIFIER_LENGTH).entityId()

    val monday = enumeration("monday", DailyAvailability::class)
    val tuesday = enumeration("tuesday", DailyAvailability::class)
    val wednesday = enumeration("wednesday", DailyAvailability::class)
    val thursday = enumeration("thursday", DailyAvailability::class)
    val friday = enumeration("friday", DailyAvailability::class)
    val saturday = enumeration("saturday", DailyAvailability::class)
    val sunday = enumeration("sunday", DailyAvailability::class)
    val startDate = varchar("start_date", MAX_IDENTIFIER_LENGTH)
    val endDate = varchar("end_date", MAX_IDENTIFIER_LENGTH)
}

class Calendar(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Calendar>(CalendarTable)

    var monday by CalendarTable.monday
    var tuesday by CalendarTable.tuesday
    var wednesday by CalendarTable.wednesday
    var thursday by CalendarTable.thursday
    var friday by CalendarTable.friday
    var saturday by CalendarTable.saturday
    var sunday by CalendarTable.sunday

    var startDate by CalendarTable.startDate
    var endDate by CalendarTable.endDate
}