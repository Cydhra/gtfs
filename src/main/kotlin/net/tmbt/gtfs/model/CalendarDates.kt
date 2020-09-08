package net.tmbt.gtfs.model

import net.tmbt.gtfs.model.CalendarDate.ServiceIdentifier.Companion.fromCalendar
import net.tmbt.gtfs.model.CalendarDate.ServiceIdentifier.Companion.fromIdentifier
import net.tmbt.gtfs.util.ByOrdinal
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.or
import java.util.*

enum class ExceptionType {
    INVALID,
    SERVICE_ADDED,
    SERVICE_REMOVED;

    companion object : ByOrdinal<ExceptionType>(values())
}

/**
 * Table representing `calendar_dates.txt` from GTFS. However, since GTFS specifies the `service_id` to be either a
 * reference or a string ID, two columns exist for this value. Exactly one of them must be present, the other one must
 * be null. The [DAO][CalendarDate] exposes the columns through a pseudo union class.
 *
 * @see [CalendarDate.ServiceIdentifier]
 */
object CalendarDateTable : IntIdTable() {
    val weakServiceId = varchar("weak_service_id", MAX_IDENTIFIER_LENGTH).nullable()
    val referenceServiceId = reference("service_id", CalendarTable).nullable()
    val date = varchar("date", MAX_IDENTIFIER_LENGTH)
    val exceptionType = enumeration("exception_type", ExceptionType::class)

    init {
        check {
            (weakServiceId.isNull() or referenceServiceId.isNull()) and
                    not(weakServiceId.isNull() and referenceServiceId.isNull())
        }
    }
}

class CalendarDate(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CalendarDate>(CalendarDateTable)

    private var weakServiceId by CalendarDateTable.weakServiceId
    private var referenceServiceId by Calendar optionalReferencedOn CalendarDateTable.referenceServiceId

    /**
     * Exposes either a [Calendar] that is referenced by this instance (i.e. this instance describes an exception) or a unique
     * identifier (i.e. this instance does not describe an exception but a non-periodic service).
     *
     * @see [ServiceIdentifier]
     */
    var serviceId: ServiceIdentifier
        get() {
            return if (weakServiceId != null) {
                ServiceIdentifier.fromIdentifier(weakServiceId!!)
            } else {
                ServiceIdentifier.fromCalendar(referenceServiceId!!)
            }
        }
        set(value) {
            if (value.identifier.isPresent) {
                weakServiceId = value.identifier.get()
                referenceServiceId = null
            } else {
                weakServiceId = null
                referenceServiceId = value.reference.get()
            }
        }

    var date by CalendarDateTable.date
    var exceptionType by CalendarDateTable.exceptionType

    /**
     * A pseudo union class of a [String] and a [Calendar]. By invariant, exactly one of both instance is present.
     * Construct them via [fromIdentifier] or [fromCalendar].
     */
    @Suppress("DataClassPrivateConstructor")
    data class ServiceIdentifier private constructor(
        val identifier: Optional<String>,
        val reference: Optional<Calendar>
    ) {
        companion object {
            /**
             * Create a [ServiceIdentifier] from an identifier that does **not** reference a [Calendar].
             */
            fun fromIdentifier(identifier: String) =
                ServiceIdentifier(Optional.of(identifier), Optional.empty())

            /**
             * Create a [ServiceIdentifier] from a reference that does link it to a [Calendar].
             */
            fun fromCalendar(calendar: Calendar) =
                ServiceIdentifier(Optional.empty(), Optional.of(calendar))
        }
    }
}

