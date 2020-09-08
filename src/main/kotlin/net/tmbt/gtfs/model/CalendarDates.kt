package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

enum class ExceptionType {
    INVALID,
    SERVICE_ADDED,
    SERVICE_REMOVED;

    companion object : ByOrdinal<ExceptionType>(values())
}

object CalendarDateTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("service_id", MAX_IDENTIFIER_LENGTH).entityId()

    val date = varchar("date", MAX_IDENTIFIER_LENGTH)
    val exceptionType = enumeration("execption_type", ExceptionType::class)
}

class CalendarDate(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, CalendarDate>(CalendarDateTable)

    var date by CalendarDateTable.date
    var exceptionType by CalendarDateTable.exceptionType
}