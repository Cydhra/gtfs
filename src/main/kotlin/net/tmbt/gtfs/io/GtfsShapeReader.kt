package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.Shape
import net.tmbt.gtfs.model.ShapePointTable
import net.tmbt.gtfs.model.ShapeTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream
import java.math.BigDecimal
import java.math.MathContext

class GtfsShapeReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {
    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            val shapeEntityId =
                EntityID(entries["shape_id"] ?: error("cannot create shape without shape id"), ShapeTable)

            if (Shape.find { ShapeTable.id eq shapeEntityId }.empty()) {
                ShapeTable.insert { row ->
                    row[id] = shapeEntityId
                }
            }

            return@transaction ShapePointTable.insertAndGetId { row ->
                row[shapeId] = shapeEntityId
                row[latitude] = entries["shape_pt_lat"]?.let { BigDecimal(it, MathContext(10)) }
                    ?: error("cannot create shape point without latitude")

                row[longitude] = entries["shape_pt_lon"]?.let { BigDecimal(it, MathContext(10)) }
                    ?: error("cannot create shape point without longitude")

                row[sequenceNumber] = entries["shape_pt_sequence"]?.toInt()
                    ?: error("cannot create shape point without sequence number")

                row[distanceTraveled] = entries["shape_dist_traveled"]?.toFloat()
            }
        }
    }
}