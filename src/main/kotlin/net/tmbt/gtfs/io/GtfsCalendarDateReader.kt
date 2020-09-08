package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.CalendarDateTable
import net.tmbt.gtfs.model.CalendarTable
import net.tmbt.gtfs.model.ExceptionType
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsCalendarDateReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            val serviceId = entries["service_id"] ?: error("cannot create calendar date without service id")

            val calendarService = CalendarTable.select { CalendarTable.id eq serviceId }

            val entityId = InsertStatement<Number>(CalendarDateTable).apply {
                // set the service id to exactly one of reference column or varchar column
                this[CalendarDateTable.referenceServiceId] = calendarService.firstOrNull()?.get(CalendarTable.id)
                this[CalendarDateTable.weakServiceId] = if (calendarService.count() > 0) null else serviceId

                this[CalendarDateTable.date] = entries["date"] ?: error("cannot create calendar date without date")
                this[CalendarDateTable.exceptionType] = ExceptionType.byOrdinalOrNull(
                    entries["exception_type"]?.toInt()
                        ?: error("cannot create calendar date without exception type")
                )!!
            }.execute(TransactionManager.current())!!

            return@transaction EntityID(entityId, CalendarDateTable)
        }
    }
}