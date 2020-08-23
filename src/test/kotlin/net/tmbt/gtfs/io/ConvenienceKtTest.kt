package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert
import org.testng.annotations.Test
import java.nio.file.Files
import java.nio.file.StandardCopyOption

internal class ConvenienceKtTest {

    @Test
    fun testDatabaseSetup() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
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

    }

    @Test(dependsOnMethods = ["testDatabaseSetup"])
    fun testImportGtfsDatasetUrl() {

    }
}