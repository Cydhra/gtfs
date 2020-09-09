package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.*
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert.*
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.reflect.KProperty

/**
 * Unit tests for the convenience features in io.Convenience.kt
 */
internal class ConvenienceKtTest {

    lateinit var dataSetPath: File

    @BeforeClass
    fun initDatabase() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        dataSetPath = createTempFile("testdata", ".zip")
        dataSetPath.deleteOnExit()

        javaClass.classLoader.getResourceAsStream("testdata.zip")!!.use {
            Files.copy(it, dataSetPath.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    @Test
    fun testDatabaseSetup() {
        transaction {
            updateDatabase()

            listOf(
                AgencyTable,
                LevelTable,
                StopTable,
                RouteTable,
                CalendarTable,
                CalendarDateTable,
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
            ).forEach { table ->
                assertTrue(table.exists())
            }
        }
    }

    @Test(dependsOnMethods = ["testDatabaseSetup"])
    fun testImportGtfsDatasetPath() {
        transaction {
            updateDatabase()
            importGtfsDataset(dataSetPath.toPath())
            testDataSet()
        }
    }

    @Test(dependsOnMethods = ["testDatabaseSetup"])
    fun testImportGtfsDatasetUrl() {
        transaction {
            updateDatabase()
            importGtfsDataset(dataSetPath.toPath().toUri().toURL(), inMemory = true)
            testDataSet()
        }
    }

    /**
     * A function to test the integrity of the imported data set. Does not test for the correctness of imported values,
     * but merely for their presence. This means they still could be wrong (e.g. values interchanged within columns) but
     * at least no columns are missing, that are present in the dataset
     */
    private fun testDataSet() {
        val agencies = Agency.all()
        assertEquals(1, agencies.count())
        assertOptionalsPresent(agencies, Agency::lang, Agency::phone)

        assertEquals(1, Calendar.all().count())
        assertEquals(1, CalendarDate.all().count())

        val routes = Route.all()
        assertEquals(1, routes.count())
        assertOptionalsPresent(routes, Route::agency, Route::shortName, Route::color, Route::textColor)

        val stopTimes = StopTime.all()
        assertEquals(1, stopTimes.count())
        assertOptionalsPresent(
            stopTimes,
            StopTime::arrivalTime,
            StopTime::departureTime,
            StopTime::pickupType,
            StopTime::dropOffType
        )

        val stops = Stop.all()
        assertEquals(4, stops.count())
        assertOptionalsPresent(
            stops,
            Stop::name,
            Stop::lat,
            Stop::lon
        )

        val trips = Trip.all()
        assertEquals(1, trips.count())
        assertOptionalsPresent(
            trips,
            Trip::headsign,
            Trip::direction,
            Trip::bikesAllowed
        )
    }

    private fun assertOptionalsPresent(entities: Iterable<Entity<*>>, vararg properties: KProperty<*>) {
        entities.forEach { entity ->
            properties.forEach { property ->
                assertNotNull(
                    property.getter.call(entity),
                    "${property.name} is not present in ${entity.javaClass.simpleName} \"${entity.id}\""
                )
            }
        }
    }
}