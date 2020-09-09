package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.StopTable
import net.tmbt.gtfs.model.TransferTable
import net.tmbt.gtfs.model.TransferType
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsTransferReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {

    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            TransferTable.insertAndGetId { row ->
                row[fromStopId] =
                    EntityID(entries["from_stop_id"] ?: error("cannot create transfer without from_stop_id"), StopTable)
                row[toStopId] =
                    EntityID(entries["to_stop_id"] ?: error("cannot create transfer without to_stop_id"), StopTable)
                row[transferType] = TransferType.byOrdinalOrNull(entries["transfer_type"]?.toInt())
                    ?: error("cannot create transfer without transfer type")
                row[minTransferTime] = entries["transfer_time"]?.toInt()
            }
        }
    }
}