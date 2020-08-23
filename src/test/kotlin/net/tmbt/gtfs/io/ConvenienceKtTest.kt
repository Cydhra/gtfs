package net.tmbt.gtfs.io

import net.tmbt.gtfs.model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
                Assertions.assertTrue(table.exists())
            }
        }
    }

    @Test
    fun testImportGtfsDatasetPath() {

    }

    @Test
    fun testImportGtfsDatasetUrl() {

    }
}