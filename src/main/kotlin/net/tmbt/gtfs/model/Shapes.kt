package net.tmbt.gtfs.model

import net.tmbt.gtfs.model.StopTable.nullable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.select

object ShapeTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = text("shape_id").entityId()

    val lat = decimal("shape_pt_lat", 10, 7).nullable()
    val lon = decimal("shape_pt_lon", 10, 7).nullable()
    val sequence = integer("shape_pt_sequence")
    val distTraveled = float("shape_dist_traveled").nullable()
}

class Shape(val id: String) {
    init {
        val result = ShapeTable.select { ShapeTable.id eq id}
    }
}