# GTFS Parser
A parser for the [General Transit Feed Specification](https://developers.google.com/transit/gtfs/reference) that generates a relational SQL model closely resembling the original GTFS 
structure.
The SQL database is generated using [Jetbrains Exposed](https://github.com/JetBrains/Exposed) with no specific SQL 
backend in mind. 
A user thus may freely chose any supported SQL driver and provide a matching database.