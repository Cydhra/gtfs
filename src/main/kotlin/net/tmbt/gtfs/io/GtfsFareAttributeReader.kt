package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.AgencyTable
import net.tmbt.gtfs.model.FareAttributeTable
import net.tmbt.gtfs.model.PaymentMethod
import net.tmbt.gtfs.model.StopTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsFareAttributeReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(
                entries["fare_id"] ?: error("cannot create fare attribute without id"),
                StopTable
            )

            FareAttributeTable.insert { row ->
                row[fareId] = entityId
                row[price] = entries["price"]?.toFloat() ?: error("cannot create fare attribute without price")
                row[currency] = entries["currency_type"] ?: error("cannot create fare attribute without currency type")
                row[paymentMethod] = entries["payment_method"]?.let { PaymentMethod.byOrdinalOrNull(it.toInt()) }
                    ?: error("cannot create fare attribute without payment method")
                row[transfers] = entries["transfers"]?.toInt()
                row[agency] = EntityID(
                    entries["agency_id"] ?: error("cannot create fare attribute without agency"),
                    AgencyTable
                )
                row[transferDuration] = entries["transfer_duration"]?.toInt()
            }

            return@transaction entityId
        }
    }
}