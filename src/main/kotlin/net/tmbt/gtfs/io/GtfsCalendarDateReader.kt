package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.CalendarDateTable
import net.tmbt.gtfs.model.CalendarTable
import net.tmbt.gtfs.model.ExceptionType
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsCalendarDateReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(
                entries["service_id"] ?: error("cannot create CalendarDate without id"),
                CalendarTable
            )

            CalendarDateTable.insert { row ->
                row[id] = entityId
                row[date] = entries["date"] ?: error("cannot create calendar date without date")
                row[exceptionType] = ExceptionType.byOrdinalOrNull(
                    entries["exception_type"]?.toInt()
                        ?: error("cannot create calendar date without exception type")
                )!!
            }

            return@transaction entityId
        }
    }
}