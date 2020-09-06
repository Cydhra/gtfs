package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object FrequencyTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = reference("trip_id", TripTable)

    val startTime = varchar("start_time", MAX_IDENTIFIER_LENGTH)
    val endTime = varchar("end_time", MAX_IDENTIFIER_LENGTH)
    val headway = integer("headway_secs")
    val exact = bool("exact_times")
}

class Frequency(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Frequency>(FrequencyTable)

    var startTime by FrequencyTable.startTime
    var endTime by FrequencyTable.endTime
    var headway by FrequencyTable.headway
    var exact by FrequencyTable.exact
}