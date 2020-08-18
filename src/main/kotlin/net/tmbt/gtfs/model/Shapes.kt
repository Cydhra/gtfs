package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object ShapeTable : IdTable<String>() {
    override val id: Column<EntityID<String>>
        get() = TODO("Not yet implemented")

}