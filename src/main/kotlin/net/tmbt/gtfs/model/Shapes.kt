package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

object ShapeTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("shape_id", MAX_IDENTIFIER_LENGTH).entityId()

    val lat = decimal("shape_pt_lat", 10, 7).nullable()
    val lon = decimal("shape_pt_lon", 10, 7).nullable()
    val sequence = integer("shape_pt_sequence")
    val distTraveled = float("shape_dist_traveled").nullable()
}

class Shape(val id: String) {
    private var _points: List<Pair<Int, LatLon?>>
    var points
        get() = _points
        set(pts) {
            transaction {
                ShapeTable.deleteWhere {ShapeTable.id eq id}
                pts.forEach { item ->
                    ShapeTable.insert {
                        it[sequence] = item.first
                        item.second?.let { item ->
                            it[lat] = item.latitude
                            it[lon] = item.longitude
                        }
                    }
                }
            }
            _points = pts.sortedBy { it.first }
        }

    init {
        _points = transaction {
            ShapeTable.select { ShapeTable.id eq id }
                .orderBy(ShapeTable.sequence to SortOrder.ASC)
                .map {
                    if (it[ShapeTable.lat] == null || it[ShapeTable.lon] == null)
                        Pair(it[ShapeTable.sequence], null)
                    else
                        Pair(it[ShapeTable.sequence], LatLon(it[ShapeTable.lat]!!, it[ShapeTable.lon]!!))
                }
        }
    }
}

data class LatLon(val latitude: BigDecimal, val longitude: BigDecimal)
