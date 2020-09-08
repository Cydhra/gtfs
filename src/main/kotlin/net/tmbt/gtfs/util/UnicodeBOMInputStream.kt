// released under the WTFPL v2 license, by Gregory Pakosz (@gpakosz)
package net.tmbt.gtfs.util

import java.io.IOException
import java.io.InputStream
import java.io.PushbackInputStream

/**
 * The `UnicodeBOMInputStream` class wraps any
 * `InputStream` and detects the presence of any Unicode BOM
 * (Byte Order Mark) at its beginning, as defined by
 * [RFC 3629 - UTF-8, a
 * transformation format of ISO 10646](http://www.faqs.org/rfcs/rfc3629.html)
 *
 *
 * The
 * [Unicode FAQ](http://www.unicode.org/unicode/faq/utf_bom.html)
 * defines 5 types of BOMs:
 *  * <pre>00 00 FE FF  = UTF-32, big-endian</pre>
 *  * <pre>FF FE 00 00  = UTF-32, little-endian</pre>
 *  * <pre>FE FF        = UTF-16, big-endian</pre>
 *  * <pre>FF FE        = UTF-16, little-endian</pre>
 *  * <pre>EF BB BF     = UTF-8</pre>
 *
 *
 *
 * Use the [.getBOM] method to know whether a BOM has been detected
 * or not.
 *
 *
 * Use the [.skipBOM] method to remove the detected BOM from the
 * wrapped `InputStream` object.
 *
 * @author Gregory Pakosz
 * @version 1.0
 */
internal class UnicodeBOMInputStream(inputStream: InputStream?) : InputStream() {
    /**
     * Type safe enumeration class that describes the different types of Unicode
     * BOMs.
     */
    class BOM private constructor(internal val bytes: ByteArray, description: String) {
        /**
         * Returns a `String` representation of this `BOM`
         * value.
         */
        override fun toString(): String {
            return description
        }

        /**
         * Returns the bytes corresponding to this `BOM` value.
         */
        fun getMarkerBytes(): ByteArray {
            val length = bytes.size
            val result = ByteArray(length)

            // make a defensive copy
            System.arraycopy(bytes, 0, result, 0, length)
            return result
        }

        private val description: String

        companion object {
            /**
             * NONE.
             */
            val NONE = BOM(byteArrayOf(), "NONE")

            /**
             * UTF-8 BOM (EF BB BF).
             */
            val UTF_8 = BOM(
                byteArrayOf(
                    0xEF.toByte(),
                    0xBB.toByte(),
                    0xBF.toByte()
                ),
                "UTF-8"
            )

            /**
             * UTF-16, little-endian (FF FE).
             */
            val UTF_16_LE = BOM(
                byteArrayOf(
                    0xFF.toByte(),
                    0xFE.toByte()
                ),
                "UTF-16 little-endian"
            )

            /**
             * UTF-16, big-endian (FE FF).
             */
            val UTF_16_BE = BOM(
                byteArrayOf(
                    0xFE.toByte(),
                    0xFF.toByte()
                ),
                "UTF-16 big-endian"
            )

            /**
             * UTF-32, little-endian (FF FE 00 00).
             */
            val UTF_32_LE = BOM(
                byteArrayOf(
                    0xFF.toByte(),
                    0xFE.toByte(),
                    0x00.toByte(),
                    0x00.toByte()
                ),
                "UTF-32 little-endian"
            )

            /**
             * UTF-32, big-endian (00 00 FE FF).
             */
            val UTF_32_BE = BOM(
                byteArrayOf(
                    0x00.toByte(),
                    0x00.toByte(),
                    0xFE.toByte(),
                    0xFF.toByte()
                ),
                "UTF-32 big-endian"
            )
        }

        init {
            assert(description.isNotEmpty()) { "invalid description: empty string is not allowed" }
            this.description = description
        }
    } // BOM

    /**
     * Skips the `BOM` that was found in the wrapped
     * `InputStream` object.
     *
     * @return this `UnicodeBOMInputStream`.
     *
     * @throws IOException when trying to skip the BOM from the wrapped
     * `InputStream` object.
     */
    @Synchronized
    @Throws(IOException::class)
    fun skipBOM(): UnicodeBOMInputStream {
        if (!skipped) {
            `in`.skip(bOM!!.bytes.size.toLong())
            skipped = true
        }
        return this
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return `in`.read()
    }

    @Throws(IOException::class, NullPointerException::class)
    override fun read(b: ByteArray): Int {
        return `in`.read(b, 0, b.size)
    }

    @Throws(IOException::class, NullPointerException::class)
    override fun read(
        b: ByteArray,
        off: Int,
        len: Int
    ): Int {
        return `in`.read(b, off, len)
    }

    @Throws(IOException::class)
    override fun skip(n: Long): Long {
        return `in`.skip(n)
    }

    @Throws(IOException::class)
    override fun available(): Int {
        return `in`.available()
    }

    @Throws(IOException::class)
    override fun close() {
        `in`.close()
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        `in`.mark(readlimit)
    }

    @Synchronized
    @Throws(IOException::class)
    override fun reset() {
        `in`.reset()
    }

    override fun markSupported(): Boolean {
        return `in`.markSupported()
    }

    private val `in`: PushbackInputStream// BOM type is immutable.

    /**
     * Returns the `BOM` that was detected in the wrapped
     * `InputStream` object.
     *
     * @return a `BOM` value.
     */
    var bOM: BOM? = null
    private var skipped = false

    init {
        if (inputStream == null) throw NullPointerException("invalid input stream: null is not allowed")
        `in` = PushbackInputStream(inputStream, 4)
        val bom = ByteArray(4)
        val read = `in`.read(bom)
        when (read) {
            4 -> {
                if (bom[0] == 0xFF.toByte() &&
                    bom[1] == 0xFE.toByte() &&
                    bom[2] == 0x00.toByte() &&
                    bom[3] == 0x00.toByte()
                ) {
                    bOM = BOM.UTF_32_LE
                } else if (bom[0] == 0x00.toByte() &&
                    bom[1] == 0x00.toByte() &&
                    bom[2] == 0xFE.toByte() &&
                    bom[3] == 0xFF.toByte()
                ) {
                    bOM = BOM.UTF_32_BE
                } else if (bom[0] == 0xEF.toByte() &&
                    bom[1] == 0xBB.toByte() &&
                    bom[2] == 0xBF.toByte()
                ) {
                    bOM = BOM.UTF_8
                } else if (bom[0] == 0xFF.toByte() &&
                    bom[1] == 0xFE.toByte()
                ) {
                    bOM = BOM.UTF_16_LE
                } else if (bom[0] == 0xFE.toByte() &&
                    bom[1] == 0xFF.toByte()
                ) {
                    bOM = BOM.UTF_16_BE
                } else {
                    bOM = BOM.NONE
                }
            }
            3 -> {
                bOM = if (bom[0] == 0xEF.toByte() &&
                    bom[1] == 0xBB.toByte() &&
                    bom[2] == 0xBF.toByte()
                ) {
                    BOM.UTF_8
                } else if (
                    bom[0] == 0xFF.toByte() &&
                    bom[1] == 0xFE.toByte()
                ) {
                    BOM.UTF_16_LE
                } else if (
                    bom[0] == 0xFE.toByte() &&
                    bom[1] == 0xFF.toByte()
                ) {
                    BOM.UTF_16_BE
                } else {
                    BOM.NONE
                }
            }
            2 -> {
                bOM = if (
                    bom[0] == 0xFF.toByte() &&
                    bom[1] == 0xFE.toByte()
                ) {
                    BOM.UTF_16_LE
                } else if (
                    bom[0] == 0xFE.toByte() &&
                    bom[1] == 0xFF.toByte()
                ) {
                    BOM.UTF_16_BE
                } else {
                    BOM.NONE
                }
            }
            else -> bOM = BOM.NONE
        }
        if (read > 0) `in`.unread(bom, 0, read)
    }
} // UnicodeBOMInputStream
