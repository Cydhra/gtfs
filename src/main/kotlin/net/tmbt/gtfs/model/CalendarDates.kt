package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

enum class ExceptionType {
    INVALID,
    SERVICE_ADDED,
    SERVICE_REMOVED
}

object CalendarDateTable : IdTable<String>() {
    val serviceId = text("service_id").entityId()
    val date = text("date")
    val exceptionType = enumeration("execption_type", ExceptionType::class)

    override val id: Column<EntityID<String>>
        get() = serviceId

}