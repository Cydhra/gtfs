package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.FareAttributeTable
import net.tmbt.gtfs.model.FareRuleTable
import net.tmbt.gtfs.model.RouteTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.InputStream

class GtfsFareRuleReader(inputStream: InputStream) : GtfsReader<Int>(inputStream) {

    override fun insertEntity(entries: Map<String, String>): EntityID<Int> {
        return transaction {
            val entityId = InsertStatement<Number>(FareRuleTable).apply {
                this[FareRuleTable.fareId] =
                    EntityID(entries["fare_id"] ?: error("cannot create fare rule without fare id"), FareAttributeTable)

                this[FareRuleTable.route] = entries["route_id"]?.let { EntityID(it, RouteTable) }
                this[FareRuleTable.origin] = entries["origin"]
                this[FareRuleTable.destination] = entries["destination"]
                this[FareRuleTable.contains] = entries["contains"]
            }.execute(TransactionManager.current())!!

            return@transaction EntityID(entityId, FareRuleTable)
        }
    }
}