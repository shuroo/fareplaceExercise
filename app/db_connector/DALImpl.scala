package db_connector

import models.GetPriceWithConnectionResults
import play.api.Logger
import play.api.libs.json.JsObject
import utils.utils.ErrorHandling

import java.sql.PreparedStatement
import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/***
 * Data Access Layer - to approach the DB.
 */
class DALImpl @Inject()(connector:JdbcConnector)  extends  DAL {

    val logger: Logger = Logger(this.getClass())

    def getPriceWithConnection(dte: String, src: String, dst: String): Future[GetPriceWithConnectionResults] = {

        val preparedStmt: PreparedStatement = connector.dbConnect().get.prepareStatement(SQLQueries.getPriceWithConnectionQuery)

        preparedStmt.setString(1, dte)
        preparedStmt.setString(2, src)
        preparedStmt.setString(3, dst)

        Future {
            val queryResult = preparedStmt.executeQuery
            print("SQL query returned the following results:", queryResult)
            GetPriceWithConnectionResults(queryResult.toString, true, "")


        }
    }.recover {
        case e: Throwable =>
            ErrorHandling.handleRecover(e, "getPriceWithConnection")
    }

    @Override
    def loadPriceRecord(row:List[String]): Future[Boolean] = {

        if (row.length < 4) {
            logger.error(s"Detected an invalid row of length:${row.length}- aborting ");
            Future.successful(false);
        }
        val preparedStmt: PreparedStatement = connector.connection.prepareStatement(SQLQueries.pricesTableRowInsertQuery)
        val flightDate = row(0)
        val flightId = row(1)
        val seats = row(2)
        val price = row(3)
        preparedStmt.setString(1, flightDate)
        preparedStmt.setString(2, flightId)
        preparedStmt.setInt(3, seats.toInt)
        preparedStmt.setDouble(4, price.toDouble)
        Future {
            preparedStmt.execute
            logger.info(s"Successfully stored row for prices of flight-id:${flightId} in db")
            preparedStmt.close()
            true
        }.recover {
            case e: Throwable =>
                ErrorHandling.handleRecover(e, "loadPriceRecord")
                false
        }
    }

    @Override
    def loadFlightRecord(row: List[String]): Future[Boolean] = {
        val preparedStmt: PreparedStatement = connector.connection.prepareStatement(SQLQueries.flightsTableRowInsertQuery)
        if (row.length < 6) {
            logger.error(s"Detected an invalid row of length:${row.length}- abort sending it to the db ");
            Future.successful(false)
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
        Future {
            preparedStmt.execute
            logger.info(s"Successfully stored flight id:${flightId} in db")
            preparedStmt.close()
            true
        }.recover {
            case e: Throwable =>
                ErrorHandling.handleRecover(e, "loadPriceRecord")
                false
        }
    }

}
