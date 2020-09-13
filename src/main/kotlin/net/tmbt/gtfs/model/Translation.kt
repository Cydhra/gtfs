package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

enum class TableName {
    AGENCY,
    STOPS,
    ROUTES,
    TRIPS,
    STOP_TIMES,
    FEED_INFO,
    PATHWAYS,
    LEVELS,
    ATTRIBUTIONS
}

object TranslationTable : IntIdTable() {
    val table = enumeration("table_name", TableName::class)
    val fieldName = varchar("field_name", MAX_IDENTIFIER_LENGTH)
    val language = varchar("language", MAX_IDENTIFIER_LENGTH)
    val recordId = varchar("record_id", MAX_IDENTIFIER_LENGTH).nullable()
    val recordSubId = varchar("record_sub_id", MAX_IDENTIFIER_LENGTH).nullable()
    val fieldValue = varchar("field_value", MAX_TEXT_LENGTH).nullable()
}

class Translation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Translation>(TranslationTable)

    var tableName by TranslationTable.table
    var fieldName by TranslationTable.fieldName
    var language by TranslationTable.language
    var recordId by TranslationTable.recordId
    var recordSubId by TranslationTable.recordSubId
    var fieldValue by TranslationTable.fieldValue
}