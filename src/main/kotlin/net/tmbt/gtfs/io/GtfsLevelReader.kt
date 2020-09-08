package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.LevelTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsLevelReader(inputStream: InputStream) : GtfsReader<String>(inputStream) {

    override fun insertEntity(entries: Map<String, String>): EntityID<String> {
        return transaction {
            val entityId = EntityID(
                entries["level_id"] ?: error("cannot create level without id"),
                LevelTable
            )

            LevelTable.insert { row ->
                row[id] = entityId
                row[levelIndex] = entries["level_index"]?.toFloat() ?: error("cannot crate level without index")
                row[levelName] = entries["level_name"]
            }

            return@transaction entityId
        }
    }
}