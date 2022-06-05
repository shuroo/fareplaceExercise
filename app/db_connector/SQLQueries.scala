package db_connector

/**
 * Class for sql queries (constants)
 * author: shirirave
 */
object SQLQueries {

    //todo: change according to sqlite... command
    val dbDropFlights = """drop table IF EXISTS Flights;""".stripMargin
    val dbDropPrices = """ drop table IF EXISTS Prices;""".stripMargin
    val dbDropFlightWithPrice = """drop view IF EXISTS FlightWithPrice;""".stripMargin
    val dbDropDirectFlights = """ drop view IF EXISTS  directFlights;""".stripMargin
    val dbDropOneConnection = """drop view IF EXISTS oneConnection;""".stripMargin
    val dbDropTwoConnections = """ drop view IF EXISTS  twoConnections;""".stripMargin


    /** *
     * Flight table format should be:
     *
     * Departure date, formatted as YYYY-MM-DD
     * Flight Number (up to 4 digits)
     * Departure airport code (3 upper case letters)
     * Arrival airport code (3 upper case letters)
     * Departure time, formatted according to ISO-8601( equivalent to https://docs.oracle.com/javase/8/docs/api/java/time/OffsetTime.html)
     * Duration of the flight in minutes
     * (The time is defined as a varchar due to the fact that the plus (+) cannot be parsed normally to a daterime)
     */
    val flightsTableCreate =
        """CREATE Table IF NOT EXISTS
                             Flights( FlightNumber varchar(5),
                             DepartureDate Date, DepartureAP varchar(4),
                            ArrivalAP varchar(4), DepartureTime VARCHAR(30), FlightDuration INT,
                             PRIMARY KEY (FlightNumber, DepartureDate)) """.stripMargin


    val flightsTableRowInsertQuery =
        """insert into Flights (DepartureDate ,FlightNumber,
         DepartureAP ,
        ArrivalAP , DepartureTime , FlightDuration )
        values (?,?,?,?,?,?)""".stripMargin

    /** *
     * Prices table format should be:
     *
     * Departure date, formatted as YYYY-MM-DD
     * Flight number (up to 4 digits)
     * Number of seats available for sale (unsigned integer)
     * Price of the flight (numeric). If zero, that means the flight cannot be sold.
     *
     */
    val pricesTableCreate =
        """CREATE Table IF NOT EXISTS Prices
                            ( DepartureDate Date, FlightNumber varchar(5),
                            NumberOfSeats int , Price double ) """.stripMargin

    val pricesTableRowInsertQuery =
        """insert into Prices ( DepartureDate, FlightNumber,
        NumberOfSeats, Price )
        values (?,?,?,?)""".stripMargin

    def getPriceWithConnectionQuery(dte: String, from: String, to: String) = {
        s"""
           | select * from directFlights
           | union
           | select * from oneConnection
           | union
           | select * from twoConnections
           | where
           | dte='${dte}' and src='${from}' and dst='${to}'
           | order by dte desc
           |""".stripMargin
    }


    /// View creations for the main EP:

    val flightWithPriceViewCreate = """ create view FlightWithPrice as
                                |    select * from Flights f join (select * from Prices where numberOfSeats > 0) p
                                |    on f.flightNumber = p.flightNumber
                                |    and f.DepartureDate = p.DepartureDate ;""".stripMargin

    val directFlightsViewCreate = """ create view directFlights as select '["'||e.FlightNumber||'"]' as FlightNumbers,
              | e.DepartureDate dte,e.Price,
              | e.DepartureAP src ,e.ArrivalAP dst,
              | '["'||e.DepartureTime ||'"]' DepartureTimes,
              | '["'||e.FlightDuration ||'"]' FlightDurations,
              | e.DepartureAP ||'-'|| e.ArrivalAP path
              | from FlightWithPrice e;
              """.stripMargin

    val oneConnectionViewCreate = """
              |create view oneConnection as select distinct '["'||e.FlightNumber||'","'||c1.FlightNumber||'"]' as FlightNumbers,
              |    e.DepartureDate dte, e.Price+c1.Price as Price,
              |    e.DepartureAP src,c1.ArrivalAP dst,'["'||e.DepartureTime ||'"-"'|| c1.DepartureTime||'"]' DepartureTimes,
              |    '["'||e.FlightDuration ||'"-"'|| c1.FlightDuration||'"]' FlightDurations,
              |    e.DepartureAP ||'-'|| e.ArrivalAP||'-'|| c1.ArrivalAP path  from
              |    FlightWithPrice e inner join FlightWithPrice c1 on e.ArrivalAP = c1.DepartureAP ;
              |
              |
              """.stripMargin

    val twoConnectionsViewCreate = """create view twoConnections as select distinct '["'||e.FlightNumber||'","'||c1.FlightNumber||'","'||c2.FlightNumber||'"]'
              |        as FlightNumbers, e.DepartureAP as src, c2.ArrivalAP as dst, e.DepartureDate dte, e.Price+c1.Price+c2.Price as Price,
              |        '['||e.DepartureTime ||'-'|| c1.DepartureTime||'-'||c2.DepartureTime||']' as DepartureTimes,
              |          '["'||e.FlightDuration ||'"-"'|| c1.FlightDuration||'"-"'|| c2.FlightDuration||'"]' FlightDurations,
              |        e.DepartureAP ||'-'|| e.ArrivalAP||'-'|| c1.ArrivalAP||'-'|| c2.ArrivalAP as path
              |        from FlightWithPrice e
              |        inner join FlightWithPrice c1 on e.ArrivalAP = c1.DepartureAP inner join FlightWithPrice c2
              |        on c1.ArrivalAP = c2.DepartureAP;
              |""".stripMargin
};