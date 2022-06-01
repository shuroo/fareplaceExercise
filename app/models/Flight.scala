package models

import db_connector.{ SQLQueries}
import db_connector.JdbcConnector.{connection}
import play.api.Logger
import java.sql.PreparedStatement
import scala.util.{Failure, Success, Try}

/**
 * Case class to represent a Flight DB record in the ORM
 */

case class Flight(flightDate:String, flightId:String,flightFrom:String,flightTo:String,
                  flightTimeStamp:String, flightDuration:Int)

object Flight {

    val logger = Logger(this.getClass())

    def loadFlightRecord(row: Seq[String]): Boolean = {
        val preparedStmt: PreparedStatement = connection.prepareStatement(SQLQueries.flightsTableRowInsertQuery)
        if(row.length < 6){
            logger.error(s"Detected an invalid row of length:${row.length}- abort sending it to the db ");
            false
        }

        val flightDate = row(0)
        val flightId = row(1)
        val flightFrom = row(2)
        val flightTo = row(3)
        val flightTimeStamp = row(4)
        val flightDuration = row(5)

        preparedStmt.setString(1, flightDate)
        preparedStmt.setString(2, flightId)
        preparedStmt.setString(3, flightFrom)
        preparedStmt.setString(4, flightTo)
        preparedStmt.setString(5, flightTimeStamp)
        preparedStmt.setInt(6, flightDuration.toInt)
        preparedStmt.execute
        logger.info(s"Successfully stored flight id:${flightId} in db")
        preparedStmt.close()
        true
    }
    /**
     * Utility method for csv
     * @return
     */
    def loadAllFlightRows(allRows:Set[List[String]])={
        allRows.map{ line=>
            Try{ loadFlightRecord(line) } match {
                case Success(reader) =>
                    // Line inserted successfully - logging is optional.
                    logger.info(s"line:${line} inserted successfully into the flights table.")
                    true;
                case Failure(ex) =>
                    val msg = s"failed to insert row into the Prices table. exception occured:${ex}}"
                    ex.printStackTrace()
                    logger.error(msg)
                    false;
            }
        }
    }
}
