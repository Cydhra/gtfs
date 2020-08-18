package net.tmbt.gtfs.model

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object TripTable : IdTable<String>() {
    val routeId = text("route_id").entityId()
    //val service = reference("service_id", ) TODO: requires calendar


    override val id: Column<EntityID<String>>
        get() = TODO("Not yet implemented")

}