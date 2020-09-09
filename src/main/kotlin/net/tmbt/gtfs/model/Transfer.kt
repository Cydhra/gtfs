package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * The `transfer_type` enumeration. The names are chosen based upon the description provided in the
 * [GTFS Reference](https://developers.google.com/transit/gtfs/reference/#transferstxt).
 */
enum class TransferType {
    RECOMMENDED_TRANSFER_POINT,
    TIMED_TRANSFER_POINT,
    MINIMUM_TIME_REQUIRED_TRANSFER_POINT,
    NO_TRANSFER_POSSIBLE;

    companion object : ByOrdinal<TransferType>(values())
}

/**
 * Table representing `transfers.txt`. However, as the GTFS does not specify a primary key an artificial integer key is
 * generated for each entry.
 */
object TransferTable : IntIdTable() {
    val fromStopId = reference("from_stop_id", StopTable)
    val toStopId = reference("to_stop_id", StopTable)
    val transferType = enumeration("transfer_type", TransferType::class)
    val minTransferTime = integer("min_transfer_time").nullable()
}

class Transfer(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Transfer>(TransferTable)

    var fromStop by Stop referencedOn TransferTable.fromStopId
    var toStop by Stop referencedOn TransferTable.toStopId
    var transferType by TransferTable.transferType
    var minTransferTime by TransferTable.minTransferTime
}