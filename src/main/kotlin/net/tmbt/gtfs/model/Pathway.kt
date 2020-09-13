package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.or

enum class PathwayMode {
    INVALID,
    WALKWAY,
    STAIRS,

    /** moving sidewalks */
    TRAVELATOR,
    ESCALATOR,

    /** requires proof of payment to cross */
    FARE_GATE,

    /** exit an area where proof of payment is required to re-enter */
    EXIT_GATE;

    companion object : ByOrdinal<PathwayMode>(values())
}

object PathwayTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("pathway_id", MAX_IDENTIFIER_LENGTH).entityId()
    val fromStopId = reference("from_stop_id", StopTable)
    val toStopId = reference("to_stop_id", StopTable)
    val pathwayMode = enumeration("pathway_mode", PathwayMode::class)
    val isBidirectional = bool("is_bidirectional")
    val length = float("length").nullable()
    val traversalTime = integer("traversal_time").nullable()
    val stairCount = integer("stair_count").nullable()
    val maxSlope = float("max_slope").nullable()
    val minWidth = float("min_width").nullable()
    val signpost = varchar("signposted_as", MAX_TEXT_LENGTH).nullable()
    val reversedSignpost = varchar("reversed_signposted_as", MAX_TEXT_LENGTH).nullable()

    init {
        check { length.isNull() or (length greaterEq 0f) }
        check { traversalTime.isNull() or (traversalTime greater 0) }
        check { stairCount.isNull() or (stairCount neq 0) }
        check { minWidth.isNull() or (minWidth greater 0f) }
    }
}

class Pathway(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Pathway>(PathwayTable)

    var fromStop by Stop referencedOn PathwayTable.fromStopId
    var toStop by Stop referencedOn PathwayTable.toStopId
    var pathwayMode by PathwayTable.pathwayMode
    var isBidirectional by PathwayTable.isBidirectional
    var length by PathwayTable.length
    var traversalTime by PathwayTable.traversalTime
    var stairCount by PathwayTable.stairCount
    var maxSlope by PathwayTable.maxSlope
    var minWidth by PathwayTable.minWidth
    var signpost by PathwayTable.signpost
    var reversedSignpost by PathwayTable.reversedSignpost
}