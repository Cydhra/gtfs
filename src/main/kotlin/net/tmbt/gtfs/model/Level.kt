package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object LevelTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("level_id", MAX_IDENTIFIER_LENGTH).entityId()
    val levelIndex = float("level_index")
    val levelName = varchar("level_name", MAX_NAME_LENGTH).nullable()
}

class Level(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Level>(LevelTable)

    var levelId by LevelTable.id
    var levelIndex by LevelTable.levelIndex
    var levelName by LevelTable.levelName
}