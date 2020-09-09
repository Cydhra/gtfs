package net.tmbt.gtfs.model

import net.tmbt.gtfs.model.ShapePointTable.distanceTraveled
import net.tmbt.gtfs.model.ShapePointTable.sequenceNumber
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.or

/**
 * Table containing only a unique index over all `shape_id` entries of `shapes.txt`.
 * Since the file contains multiple entries per id (for each point on a shape),
 * this table normalizes the SQL layout by extracting the id's into their own table.
 * This eases referencing of a shape and collecting the shape points using the [DAO][Shape].
 */
object ShapeTable : IdTable<String>() {
    override val id = varchar("shape_id", MAX_IDENTIFIER_LENGTH).entityId()
}

/**
 * Table representing `shapes.txt`. However, as the GTFS does not specify a primary key an artificial integer key is
 * generated for each entry.
 *
 * The requirements for [sequenceNumber] and [distanceTraveled] specified in the GTFS are implemented as check constraints.
 */
object ShapePointTable : IntIdTable() {
    val shapeId = reference("shape_id", ShapeTable)
    val latitude = decimal("shape_pt_lat", 10, 7)
    val longitude = decimal("shape_pt_lon", 10, 7)
    val sequenceNumber = integer("shape_pt_sequence")
    val distanceTraveled = float("shape_dist_traveled").nullable()

    init {
        check { sequenceNumber greaterEq 0 }
        check { distanceTraveled.isNull() or (distanceTraveled greaterEq 0f) }
    }
}

/**
 * A shape as defined by multiple points in `shapes.txt`. To get all [ShapePoint]s associated with this shape,
 * use [shapePoints].
 */
class Shape(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Shape>(ShapeTable)

    /**
     * Retrieve a list of all points associated with this shape ordered by their [ShapePoint.sequenceNumber]
     */
    val shapePoints: List<ShapePoint>
        get() = ShapePoint
            .find { ShapePointTable.shapeId eq id }
            .sortedBy { sequenceNumber }
}

/**
 * A point in a shape from the [ShapePointTable]. It defines a geo-position using [latitude] and [longitude] and is associated
 * with more points using its [shapeId].
 *
 * The [id] defined in the entity is an artificial id for the SQL database and is not referred to by the GTFS.
 */
class ShapePoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ShapePoint>(ShapePointTable)

    var shape by Shape referencedOn ShapePointTable.shapeId
    var latitude by ShapePointTable.latitude
    var longitude by ShapePointTable.longitude
    var sequenceNumber by ShapePointTable.sequenceNumber
    var distanceTraveled by ShapePointTable.distanceTraveled
}