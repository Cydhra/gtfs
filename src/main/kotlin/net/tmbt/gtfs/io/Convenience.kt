package net.tmbt.gtfs.io

import com.google.common.jimfs.Jimfs
import net.tmbt.gtfs.model.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

/**
 * Create all missing GTFS tables and add all missing columns to existing tables in order to match the latest layout expected by
 * the parser. The tables are created using the thread-local database connection.
 * This method uses JDBC metadata and thus might be a bit slow, but can be used to create or update a database once every
 * application startup.
 */
fun updateDatabase() {
    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            AgencyTable,
            LevelTable,
            StopTable,
            RouteTable,
            CalendarTable,
            CalendarDateTable,
            ShapeTable,
            ShapePointTable,
            TripTable,
            StopTimeTable,
            FareAttributeTable,
            FareRuleTable,
            FrequencyTable,
            TransferTable,
            PathwayTable,
            FeedInfoTable,
            TranslationTable,
            AttributionTable
        )
    }
}

/**
 * Import an entire folder containing all files required by the GTFS specification.
 *
 * This method assumes that all files are present if required by specification and are properly named.
 * See also [the GTFS reference](https://developers.google.com/transit/gtfs/reference#dataset_files).
 * In particular, it does not verify whether required files are actually present, nor does it check conditions of
 * conditionally required files.
 *
 * The generated SQL entities are not returned.
 * Use the [GtfsReader] implementations to obtain the generated entities.
 *
 * @param folder a [File] pointing to a directory that contains a full GTFS dataset
 */
fun importGtfsDataset(folder: Path) {
    FileSystems.newFileSystem(folder).use { fs ->
        importGtfsDataset(fs)
    }
}

/**
 * Import a GTFS archive from a zip file that is loaded from [URL].
 * The file is downloaded regardless of the protocol, so this method is to be used for GTFS datasets accessible via
 * network.
 * If [inMemory] is false, the file is downloaded into a temporary file and then imported.
 * Otherwise, it is downloaded into memory and loaded from there.
 * The latter option assumes that enough memory is available for the VM.
 *
 * This method assumes that all files are present if required by specification and are properly named.
 * See also [the GTFS reference](https://developers.google.com/transit/gtfs/reference#dataset_files).
 * In particular, it does not verify whether required files are actually present, nor does it check conditions of
 * conditionally required files.
 *
 * The generated SQL entities are not returned.
 * Use the [GtfsReader] implementations to obtain the generated entities.
 *
 * @param url locator for a zipped GTFS dataset
 * @param inMemory if true, the zip archive will not be written to disk
 */
fun importGtfsDataset(url: URL, inMemory: Boolean = false) {
    if (inMemory) {
        Jimfs.newFileSystem().use { fileSystem ->
            val tempFile = fileSystem.getPath("gtfsdataset.zip")
            val outputStream = Files.newOutputStream(tempFile)
            url.openStream().use { it.copyTo(outputStream) }
            outputStream.close()
            importGtfsDataset(FileSystems.newFileSystem(tempFile))
        }
    } else {
        val tempFile = Files.createTempFile("gtfsdataset", ".zip")
        val outputStream = Files.newOutputStream(tempFile)
        url.openStream().use { it.copyTo(outputStream) }
        outputStream.close()
        importGtfsDataset(FileSystems.newFileSystem(tempFile))
        Files.delete(tempFile)
    }
}

/**
 * Import an entire GTFS dataset from a given [FileSystem].
 *
 * This method assumes that all files are present if required by specification and are properly named.
 * See also [the GTFS reference](https://developers.google.com/transit/gtfs/reference#dataset_files).
 * In particular, it does not verify whether required files are actually present, nor does it check conditions of
 * conditionally required files.
 *
 * The generated SQL entities are not returned.
 * Use the [GtfsReader] implementations to obtain the generated entities.
 */
fun importGtfsDataset(fileSystem: FileSystem) {
    transaction {
        readEntitiesFrom(fileSystem, "agency.txt", ::GtfsAgencyReader, required = true)
        readEntitiesFrom(fileSystem, "levels.txt", ::GtfsLevelReader)
        readEntitiesFrom(fileSystem, "stops.txt", ::GtfsStopsReader, required = true)
        readEntitiesFrom(fileSystem, "routes.txt", ::GtfsRoutesReader, required = true)
        readEntitiesFrom(fileSystem, "calendar.txt", ::GtfsCalendarReader)
        readEntitiesFrom(fileSystem, "calendar_dates.txt", ::GtfsCalendarDateReader)
        readEntitiesFrom(fileSystem, "shapes.txt", ::GtfsShapeReader)
        readEntitiesFrom(fileSystem, "trips.txt", ::GtfsTripReader, required = true)
        readEntitiesFrom(fileSystem, "stop_times.txt", ::GtfsStopTimeReader, required = true)
        readEntitiesFrom(fileSystem, "fare_attributes.txt", ::GtfsFareAttributeReader)
        readEntitiesFrom(fileSystem, "fare_rules.txt", ::GtfsFareRuleReader)
        readEntitiesFrom(fileSystem, "frequencies.txt", ::GtfsFrequenciesReader)
        readEntitiesFrom(fileSystem, "transfers.txt", ::GtfsTransferReader)
        readEntitiesFrom(fileSystem, "pathways.txt", ::GtfsPathwayReader)
        readEntitiesFrom(fileSystem, "feed_info.txt", ::GtfsFeedInfoReader)
        readEntitiesFrom(fileSystem, "translations.txt", ::GtfsTranslationReader)
        readEntitiesFrom(fileSystem, "attributions.txt", ::GtfsAttributionReader)
    }
}

/**
 * Read the GTFS entities from a specified file using the reader generated by the provided constructor.
 *
 * @param fileSystem a file system providing the archive file
 * @param fileName name of the GTFS data file
 * @param readerConstructor a constructor for a [GtfsReader] that can read the given file
 * @param required whether this file is required by specification. If true, the method will fail, if the file is missing.
 */
private fun <T> readEntitiesFrom(
    fileSystem: FileSystem,
    fileName: String,
    readerConstructor: (InputStream) -> GtfsReader<T>,
    required: Boolean = false
) where T : Comparable<T> {
    val path = fileSystem.getPath(fileName)
    if (required || Files.exists(path)) {
        readerConstructor(Files.newInputStream(path)).use { transaction { it.readRemainingEntities() } }
    }
}