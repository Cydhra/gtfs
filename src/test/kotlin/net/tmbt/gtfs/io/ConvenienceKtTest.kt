package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Unit tests for the convenience features in io.Convenience.kt
 */
internal class ConvenienceKtTest {

    lateinit var dataSetPath: File

    @BeforeClass
    fun initDatabase() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        dataSetPath = createTempFile("kvv", ".zip")
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
                ShapeTable,
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
                Assert.assertTrue(table.exists())
            }
        }
    }

    @Test(dependsOnMethods = ["testDatabaseSetup"])
    fun testImportGtfsDatasetPath() {
        transaction {
            updateDatabase()

            importGtfsDataset(dataSetPath.toPath())

            // TODO test presence of dataset
        }
    }

    @Test(dependsOnMethods = ["testDatabaseSetup"])
    fun testImportGtfsDatasetUrl() {
        transaction {
            updateDatabase()

            importGtfsDataset(dataSetPath.toPath().toUri().toURL(), inMemory = true)

            // TODO test presence of dataset
        }
    }
}