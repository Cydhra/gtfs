package net.tmbt.gtfs.util

/**
 * Utility for enumerations to add a method to their companion object to parse an enumaration-constant from a nullable
 * ordinal
 */
open class ByOrdinal<T : Enum<T>> internal constructor(private val values: Array<T>) {
    fun byOrdinalOrNull(ordinal: Int?): T? {
        return if (ordinal == null)
            null
        else
            this.values[ordinal]
    }
}