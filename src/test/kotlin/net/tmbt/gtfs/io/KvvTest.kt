package net.tmbt.gtfs.io

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.BeforeClass
import org.testng.annotations.Ignore
import org.testng.annotations.Test
import java.net.URL

const val KVV_DATA_URL = "https://projekte.kvv-efa.de/GTFS/google_transit.zip"

/**
 * Unit tests for real-world data. By default the test is ignored, because it depends on the existance of data outside
 * of our control
 */
@Ignore
internal class KvvTest {

    @BeforeClass
    fun initDatabase() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
    }

    @Test
    fun testImportKvvData() {
        transaction {
            updateDatabase()
            importGtfsDataset(URL(KVV_DATA_URL))
        }
    }
}