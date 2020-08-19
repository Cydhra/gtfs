package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.CalendarTable
import net.tmbt.gtfs.model.DailyAvailability
import net.tmbt.gtfs.model.StopTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsCalendarReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(
                entries["service_id"] ?: error("cannot create calendar without id"),
                StopTable
            )

            CalendarTable.insert { row ->
                row[serviceId] = entityId
                row[monday] = DailyAvailability.byOrdinalOrNull(entries["monday"]?.toInt())
                    ?: error("missing monday in calendar entry")
                row[tuesday] = DailyAvailability.byOrdinalOrNull(entries["tuesday"]?.toInt())
                    ?: error("missing tuesday in calendar entry")
                row[wednesday] = DailyAvailability.byOrdinalOrNull(entries["wednesday"]?.toInt())
                    ?: error("missing wednesday in calendar entry")
                row[thursday] = DailyAvailability.byOrdinalOrNull(entries["thursday"]?.toInt())
                    ?: error("missing thursday in calendar entry")
                row[friday] = DailyAvailability.byOrdinalOrNull(entries["friday"]?.toInt())
                    ?: error("missing friday in calendar entry")
                row[saturday] = DailyAvailability.byOrdinalOrNull(entries["saturday"]?.toInt())
                    ?: error("missing saturday in calendar entry")
                row[sunday] = DailyAvailability.byOrdinalOrNull(entries["sunday"]?.toInt())
                    ?: error("missing sunday in calendar entry")

                row[startDate] = entries["start_date"] ?: error("missing start_date in calendar entry")
                row[endDate] = entries["end_date"] ?: error("missing end_date in calendar entry")
            }

            return@transaction entityId
        }
    }
}