# GTFS to SQL Parser
A parser for the [General Transit Feed Specification](https://developers.google.com/transit/gtfs/reference) that generates a relational SQL model closely resembling the original GTFS 
structure.
The SQL database is generated using [Jetbrains Exposed](https://github.com/JetBrains/Exposed) with no specific SQL 
backend in mind. 
A user thus may freely choose any supported SQL driver and provide a matching database.

# Usage
The client must provide a database connection and a transaction before calling the library.
For example, this is how to provide an in-memory database using [H2](https://h2database.com/html/main.html).
````Kotlin
Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
````
Consult the [Exposed documentation](https://github.com/JetBrains/Exposed/wiki) for more information.

To read a single GTFS file, use the corresponding reader class:
````Kotlin
GtfsAgencyReader(Files.newInputStream(File("path/to/file"))).use { reader ->
    transaction { reader.readRemainingEntities() }
 }
````
It mainly provides the methods ``nextEntity()`` and ``readRemainingEntities()`` 
which will read a single entity (and respectively all remaining entities) of the file into database
and return the corresponding ``EntityID``.
Keep in mind, that many GTFS tables reference other tables, and your SQL model will refuse to insert records
into the database, if the referenced records are not available.

To read an entire dataset into your database, you can use the convenience methods provided as follows:
````Kotlin
transaction {
    updateDatabase() // create all missing tables and columns in the connected database
    importGtfsDataset(File("path/to/dataset").toPath()) // import from a folder
    // or
    // import from a zip file
    importGtfsDataset(URL("https://projekte.kvv-efa.de/GTFS/google_transit.zip"), inMemory = false)
}
````

For further documentation, you can generate the Kotlin Doc using ``gradle dokkaHtml``.

# Future Improvements
- feed_info.txt should contain only one entry to my knowledge. 
Atm we simply import it into another SQL table.
However, no entry in the whole GTFS dataset even references it,
thus when importing two datasets, the information becomes completely useless, 
as different entries cannot be linked to any feed_info structure.
Maybe we should approach this file differently.
- We cannot write the database back into a GTFS dataset. 
This shouldn't be too hard, it is just a lot of writing to do.
- Unify the format of exception descriptions.
- translations.txt references are not evaluated