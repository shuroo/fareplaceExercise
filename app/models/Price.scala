package models

import db_connector.{JdbcConnector, SQLQueries}
import db_connector.JdbcConnector.connection
import play.api.Logger

import java.sql.PreparedStatement
import scala.util.{Failure, Success, Try}

/**
 * Case class to represent a Flight Price DB record in the ORM
 */
case class Price(flightDate:String, flightId:String, numberOfSeats:Int, price:Int)

object Price{

    val logger = Logger(this.getClass())

    def loadPriceRecord(row:List[String]): Boolean ={

        if(row.length < 4){
            logger.error(s"Detected an invalid row of length:${row.length}- aborting ");
            false;
        }
        val preparedStmt: PreparedStatement = connection.prepareStatement(SQLQueries.pricesTableRowInsertQuery)
        val flightDate = row(0)
        val flightId = row(1)
        val seats = row(2)
        val price = row(3)
        preparedStmt.setString (1, flightDate)
        preparedStmt.setString (2, flightId)
        preparedStmt.setInt (3, seats.toInt)
        preparedStmt.setDouble (4, price.toDouble)
        preparedStmt.execute
        print(s"Successfully stored row for prices of flight-id:${flightId} in db")
        preparedStmt.close()
        true;
    }

    def loadAllPriceRows(allRows:Set[List[String]]): Set[Boolean] ={
        allRows.map{ line=>
            Try{ loadPriceRecord(line)} match {
                case Success(isSuccess) =>
                    // Line inserted successfully - logging is optional.
                    logger.info(s"line:${line} was inserted into the flights table. insertion status was:${isSuccess}")
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
