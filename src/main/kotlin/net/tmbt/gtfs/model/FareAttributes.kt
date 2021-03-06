package net.tmbt.gtfs.model

import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

enum class PaymentMethod {
    ONBOARD,
    BEFORE_BOARDING;

    companion object : ByOrdinal<PaymentMethod>(values())
}

object FareAttributeTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("fare_id", MAX_IDENTIFIER_LENGTH).entityId()

    val price = float("price")
    val currency = varchar("currency_type", MAX_IDENTIFIER_LENGTH)
    val paymentMethod = enumeration("payment_method", PaymentMethod::class)
    val transfers = integer("transfers").nullable()
    val agency = reference("agency_id", AgencyTable)
    val transferDuration = integer("transfer_duration").nullable()
}

class FareAttribute(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, FareAttribute>(FareAttributeTable)

    var price by FareAttributeTable.price
    var currency by FareAttributeTable.currency
    var paymentMethod by FareAttributeTable.paymentMethod
    var transfers by FareAttributeTable.transfers
    var agency by FareAttributeTable.agency
    var transferDuration by FareAttributeTable.transferDuration
}