package db_connector

import java.sql.PreparedStatement

/**
 * Class for sql queries (constants)
 * author: shirirave
 */
object SQLQueries {

    val dbCreate = """CREATE DATABASE IF NOT EXISTS fareplace_ex_db""".stripMargin

    val dbUse = """Use fareplace_ex_db""".stripMargin

    /***
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
    val flightsTableCreate = """CREATE Table IF NOT EXISTS
                             Flights( FlightNumber varchar(5),
                             DepartureDate Date, DepartureAP varchar(4),
                            ArrivalAP varchar(4), DepartureTime VARCHAR(30), FlightDuration INT,
                             PRIMARY KEY (FlightNumber, DepartureDate)) """.stripMargin


    val flightsTableRowInsertQuery =
        """insert into Flights (DepartureDate ,FlightNumber,
         DepartureAP ,
        ArrivalAP , DepartureTime , FlightDuration )
        values (?,?,?,?,?,?)""".stripMargin

    /***
     * Prices table format should be:
     *
     * Departure date, formatted as YYYY-MM-DD
     * Flight number (up to 4 digits)
     * Number of seats available for sale (unsigned integer)
     * Price of the flight (numeric). If zero, that means the flight cannot be sold.

     */
    val pricesTableCreate = """CREATE Table IF NOT EXISTS Prices
                            ( DepartureDate Date, FlightNumber varchar(5),
                            NumberOfSeats int , Price int ,
                             PRIMARY KEY (DepartureDate,FlightNumber))""".stripMargin

    val pricesTableRowInsertQuery =
        """insert into Prices ( DepartureDate, FlightNumber,
        NumberOfSeats, Price )
        values (?,?,?,?)""".stripMargin

}
