package net.tmbt.gtfs.model

import net.tmbt.gtfs.model.ServiceIdentifier.Companion.fromCalendar
import net.tmbt.gtfs.model.ServiceIdentifier.Companion.fromCalendarDate
import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.or
import java.util.*

enum class TripDirection {
    OUTBOUND,
    INBOUND;

    companion object : ByOrdinal<TripDirection>(values())
}

object TripTable : IdTable<String>() {
    val route = reference("route_id", RouteTable)
    val serviceCalendar = reference("service_id_cal", CalendarTable).nullable()
    val serviceCalendarDate = reference("service_id_cal_date", CalendarDateTable).nullable()
    val headsign = text("trip_headsign").nullable()
    val shortName = text("trip_short_name").nullable()
    val direction = enumeration("direction_id", TripDirection::class).nullable()
    val block = text("block_id").nullable()
    val shape = reference("shape_id", ShapeTable).nullable()
    val wheelchair = enumeration("wheelchair_accessible", Availability::class).nullable()
    val bikesAllowed = enumeration("bikes_allowed", Availability::class).nullable()

    override val id: Column<EntityID<String>> = text("trip_id").entityId()

    init {
        check { serviceCalendar.isNotNull() or serviceCalendarDate.isNotNull() }
    }
}

class Trip(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Trip>(TripTable)

    private var serviceIdCalendar by Calendar optionalReferencedOn TripTable.serviceCalendar
    private var serviceIdCalendarDate by CalendarDate optionalReferencedOn TripTable.serviceCalendarDate

    /**
     * caches the value of [serviceId] so it returns the same instance as long as [serviceIdCalendar]
     * and [serviceIdCalendarDate] are not changed
     */
    private var serviceIdCache: ServiceIdentifier? = null

    /**
     * The service id as a union of a [Calendar] and a [CalendarDate], depending on which is present. One of them must
     * be present and they won't be both present.
     *
     * @see ServiceIdentifier
     */
    // Though this is a delegated property, it is guaranteed to return the
    // same instance of the union until it is replaced with a new value. This is done using the cache property above
    var serviceId: ServiceIdentifier
        get() {
            // if no identifier union has been created (i.e. this instance has been retrieved freshly from database),
            // create one
            if (serviceIdCache != null) {
                serviceIdCache =
                    if (serviceIdCalendar != null)
                        ServiceIdentifier.fromCalendar(serviceIdCalendar!!)
                    else
                        ServiceIdentifier.fromCalendarDate(serviceIdCalendarDate!!)
            }
            return serviceIdCache!!
        }
        set(value) {
            serviceIdCache = value
            if (value.calendar.isPresent) {
                serviceIdCalendar = value.calendar.get()
            } else {
                serviceIdCalendarDate = value.calendarDate.get()
            }
        }

    var route by TripTable.route
    var trip by TripTable.id
    var headsign by TripTable.headsign
    var shortName by TripTable.shortName
    var direction by TripTable.direction
    var block by TripTable.block
    var shape by TripTable.shape
    var wheelchair by TripTable.wheelchair
    var bikesAllowed by TripTable.bikesAllowed
}

/**
 * An effective union class of a [Calendar] and a [CalendarDate]. By invariant, exactly one of both instance is present.
 * Construct them via [fromCalendar] or [fromCalendarDate].
 */
data class ServiceIdentifier private constructor(
    val calendar: Optional<Calendar>,
    val calendarDate: Optional<CalendarDate>
) {
    companion object {
        /**
         * Construct a service identifier from a [Calendar]
         */
        fun fromCalendar(calendar: Calendar): ServiceIdentifier =
            ServiceIdentifier(Optional.of(calendar), Optional.empty())

        /**
         * Construct a service identifier from a [CalendarDate]
         */
        fun fromCalendarDate(calendarDate: CalendarDate): ServiceIdentifier =
            ServiceIdentifier(Optional.empty(), Optional.of(calendarDate))
    }
}