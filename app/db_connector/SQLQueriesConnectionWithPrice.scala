//package db_connector
//
//class SQLQueriesConnectionWithPrice {
//
//
//    // Queries for get connection with price:
//    val clearConnectionViewsQuery = """
//                                 |DROP VIEW IF EXISTS Connections_level_1;
//                                 |DROP VIEW IF EXISTS Connections_level_2;
//                                 |DROP VIEW IF EXISTS Connections_level_3;
//                                 |DROP VIEW IF EXISTS All_connections""".stripMargin
//
//    // Queries for get connection with price:
//    val createConnectionViewsQuery = """
//                                 CREATE VIEW  Connections_level_1 AS select * from Flights
//                                  |where DepartureDate='?'
//                                  |and DepartureAP='?' ;
//                                  |
//                                  |CREATE VIEW Connections_level_2 AS
//                                  |select * from Flights f where
//                                  | f.DepartureAP in (select ArrivalAP from Connections_level_1);
//                                  |
//                                  |CREATE VIEW Connections_level_3 AS select * from Flights where
//                                  | DepartureAP in (select ArrivalAP from Connections_level_2 )
//                                  | and DepartureAP = '?' ;
//                                  """.stripMargin
//
//    // TODO: should be parameterised values (- functions!)
//    val fetchConnectionsDataQuery = """
//                                 |select distinct * from ((select distinct * from Connections_level_3
//                                 |where DepartureAP = '?')  union
//                                 |(select distinct * from Connections_level_2  )union
//                                 |(select distinct * from Connections_level_1 ) ) f join Prices p
//                                 |	on f.FlightNumber = p.FlightNumber and f.DepartureDate = p.DepartureDate""".stripMargin
//
//
//
//}
