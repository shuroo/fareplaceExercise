package db_connector

import models.GetPriceWithConnectionResults
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import utils.DateUtils
import utils.utils.ErrorHandling

import java.sql.PreparedStatement
import javax.inject.Inject
import scala.collection.IterableOnce.iterableOnceExtensionMethods
import scala.collection.immutable.Nil.toVector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

/** *
 * Data Access Layer - to approach the DB.
 */
class DALImpl @Inject()(connector: JdbcConnector) extends DAL {

    val logger: Logger = Logger(this.getClass())

    def getPriceWithConnection(dte: String, src: String, dst: String): Future[GetPriceWithConnectionResults] = {

        // "A table or column name can't be used as a parameter to PreparedStatement. It must be hard coded."
        // from: https://stackoverflow.com/questions/41373232/sqlite-query-with-parameters-not-working-in-java
        val maybeConn = connector.dbConnect()
        maybeConn match {
            case Some(conn) =>
                // http://localhost:9000/itinerary/priceWithConnection/2020-01-01/tlv/kel

                val srcFormatted = src.toUpperCase()
                val dstFormatted = dst.toUpperCase()
                val dteFormatted = DateUtils.formatDateParam(dte)
                val query = SQLQueries.getPriceWithConnectionQuery(dteFormatted,srcFormatted,dstFormatted)
                val sttment = conn.prepareStatement(query)
                print("query:"+query)
                Future {
                    val maybeRs = Try{sttment.executeQuery()}
                    maybeRs match {
                        case Success(rs) =>

                            val resJsons = Iterator
                                .continually(rs.next)
                                .takeWhile(identity)
                                .foldLeft(Seq.empty[JsObject]) { (lst,nxt) => lst :+ Json.obj("FlightNumbers" -> rs.getString(1) ,
                                    "Path" -> rs.getString(2),
                                    "Price" -> rs.getString(3),
                                    "FlightDuration" -> rs.getString(4),
                                "DepartureTime"->rs.getString(5))

                                }

                                print("^^^results:",resJsons)
                            GetPriceWithConnectionResults(resJsons, true, "")
                        case Failure(e)=>
                            val msg = s"Failed to execute select query on - getPriceWithConnection, error:${e.getMessage}"
                            logger.error(msg)
                            ErrorHandling.handleRecover(e, "getPriceWithConnection")
                            GetPriceWithConnectionResults(List.empty[JsObject], false, msg)
                        }}.recover {
                    case e: Throwable =>
                        ErrorHandling.handleRecover(e, "getPriceWithConnection")
                }
            case None =>
                val msg = "Failed to fetch DB connection - aborting.";
                logger.error(msg)
                Future.successful(GetPriceWithConnectionResults(Seq.empty[JsObject], false, msg))
        }
    }
                @Override
                def loadPriceRecord(row: List[String]): Future[Boolean] = {

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
