package net.tmbt.gtfs.io

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Ignore
import org.testng.annotations.Test
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

const val KVV_DATA_URL = "https://projekte.kvv-efa.de/GTFS/google_transit.zip"

/**
 * Unit tests for real-world data. By default the test is ignored, because it depends on the existence of data outside
 * of our control. This test case does not check any of the imported data against the original dataset, because the
 * dataset and even its layout may change at any time.
 * This test case solely checks, whether the real-world dataset can be imported into the database layout
 * without violating its constraints.
 */
@Ignore
internal class KvvTest {

    @BeforeClass
    fun initDatabase() {
        // file based database to avoid out of memory problems
        Database.connect("jdbc:h2:file:./testdb", driver = "org.h2.Driver", user = "root", password = "")
    }

    @AfterClass
    fun deleteDatabase() {
        Files.delete(Path.of("./testdb.mv.db"))
    }

    @Test
    fun testImportKvvData() {
        transaction {
            updateDatabase()
            importGtfsDataset(URL(KVV_DATA_URL))
        }
    }
}