package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.PathwayMode
import net.tmbt.gtfs.model.PathwayTable
import net.tmbt.gtfs.model.StopTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsPathwayReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId =
                EntityID(entries["pathway_id"] ?: error("cannot create pathway without pathway_id"), PathwayTable)

            PathwayTable.insert { row ->
                row[id] = entityId
                row[fromStopId] = EntityID(
                    entries["from_stop_id"]
                        ?: error("cannot create pathway without from_stop_id"), StopTable
                )
                row[toStopId] = EntityID(
                    entries["to_stop_id"]
                        ?: error("cannot create pathway without to_stop_id"), StopTable
                )
                row[pathwayMode] = PathwayMode.byOrdinalOrNull(entries["pathway_mode"]?.toInt())
                    ?: error("cannot create pathway without pathway_mode")
                row[isBidirectional] = entries["is_bidirectional"]?.let { it.toInt() == 1 }
                    ?: error("cannot create pathway without is_bidirectional")
                row[length] = entries["length"]?.toFloat()
                row[traversalTime] = entries["traversal_time"]?.toInt()
                row[stairCount] = entries["stair_count"]?.toInt()
                row[maxSlope] = entries["max_slope"]?.toFloat()
                row[minWidth] = entries["min_width"]?.toFloat()
                row[signpost] = entries["signposted_as"]
                row[reversedSignpost] = entries["reversed_signposted_as"]
            }

            return@transaction entityId
        }
    }
}