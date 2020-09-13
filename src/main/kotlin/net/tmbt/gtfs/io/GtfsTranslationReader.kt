package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.TableName
import net.tmbt.gtfs.model.TranslationTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsTranslationReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            TranslationTable.insertAndGetId { row ->
                row[table] = TableName.valueOf(
                    entries["table_name"]?.toUpperCase()
                        ?: error("cannot create translation without table_name")
                )
                row[fieldName] = entries["field_name"]
                    ?: error("cannot create translation without field_name")
                row[language] = entries["language"]
                    ?: error("cannot create translation without language")
                row[recordId] = entries["record_id"]
                row[recordSubId] = entries["record_sub_id"]
                row[fieldValue] = entries["field_value"]
            }
        }
    }
}