package net.tmbt.gtfs.model

import net.tmbt.gtfs.model.Trip.ServiceIdentifier.Companion.fromCalendar
import net.tmbt.gtfs.model.Trip.ServiceIdentifier.Companion.fromCalendarDate
import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.or
import java.util.*

enum class TripDirection {
    OUTBOUND,
    INBOUND;

    companion object : ByOrdinal<TripDirection>(values())
}

/**
 * Table representing `trips.txt` from GTFS. However, since GTFS specifies the `service_id` to be either a
 * reference to [CalendarTable] or to [CalendarDateTable], two columns exist for this value.
 * Exactly one of them must be present, the other one must be null. The [DAO][CalendarDate] exposes the columns
 * through a pseudo union class.
 *
 * @see [Trip.ServiceIdentifier]
 */
object TripTable : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("trip_id", MAX_IDENTIFIER_LENGTH).entityId()

    val route = reference("route_id", RouteTable)

    /** referencing an id from [CalendarTable]. If null, there must be an entry in [serviceCalendarDate] column */
    val serviceCalendar = reference("service_id_cal", CalendarTable).nullable()

    /** referencing an id from [CalendarDateTable]. If null, there must be an entry in [serviceCalendar] column */
    val serviceCalendarDate = reference("service_id_cal_date", CalendarDateTable).nullable()

    val headsign = varchar("trip_headsign", MAX_TEXT_LENGTH).nullable()
    val shortName = varchar("trip_short_name", MAX_TEXT_LENGTH).nullable()
    val direction = enumeration("direction_id", TripDirection::class).nullable()
    val block = varchar("block_id", MAX_IDENTIFIER_LENGTH).nullable()
    val shape = reference("shape_id", ShapeTable).nullable()
    val wheelchair = enumeration("wheelchair_accessible", Availability::class).nullable()
    val bikesAllowed = enumeration("bikes_allowed", Availability::class).nullable()

    init {
        check {
            (serviceCalendar.isNull() or serviceCalendarDate.isNull()) and
                    not(serviceCalendar.isNull() and serviceCalendarDate.isNull())
        }
    }
}

class Trip(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, Trip>(TripTable)

    private var serviceIdCalendar by Calendar optionalReferencedOn TripTable.serviceCalendar
    private var serviceIdCalendarDate by CalendarDate optionalReferencedOn TripTable.serviceCalendarDate

    /**
     * The service id as a union of a [Calendar] and a [CalendarDate], depending on which is present. One of them must
     * be present and they won't be both present.
     *
     * @see ServiceIdentifier
     */
    var serviceId: ServiceIdentifier
        get() {
            return if (serviceIdCalendar != null)
                ServiceIdentifier.fromCalendar(serviceIdCalendar!!)
            else
                ServiceIdentifier.fromCalendarDate(serviceIdCalendarDate!!)
        }
        set(value) {
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

    /**
     * A pseudo union class of a [Calendar] and a [CalendarDate]. By invariant, exactly one of both instance is present.
     * Construct them via [fromCalendar] or [fromCalendarDate].
     */
    @Suppress("DataClassPrivateConstructor")
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
}

