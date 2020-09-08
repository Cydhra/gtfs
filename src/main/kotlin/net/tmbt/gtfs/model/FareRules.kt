package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * Table for fare rules as specified by GTFS.
 *
 * Fare rules are identified by [Int] ids, because there is no primary key specified by GTFS. Using fare_id as primary
 * key does not work, since multiple rules can apply to the same fare attribute.
 */
object FareRuleTable : IntIdTable() {
    val fareId = reference("fare_id", FareAttributeTable)
    val route = reference("route_id", RouteTable).nullable()
    val origin = varchar("origin_id", MAX_IDENTIFIER_LENGTH).nullable()
    val destination = varchar("destination_id", MAX_IDENTIFIER_LENGTH).nullable()
    val contains = varchar("contains_id", MAX_IDENTIFIER_LENGTH).nullable()
}

class FareRule(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FareRule>(FareRuleTable)

    var fareId by FareAttribute referencedOn FareRuleTable.fareId
    var route by Route optionalReferencedOn FareRuleTable.route
    var origin by FareRuleTable.origin
    var destination by FareRuleTable.destination
    var contains by FareRuleTable.contains
}