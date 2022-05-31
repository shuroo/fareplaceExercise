package db_connector

import java.sql.{Connection, DriverManager, PreparedStatement}
import scala.util.{Failure, Success, Try}
import utils.Constants
import play.api.Logger
/**
 * A Scala JDBC connection example, based on the example given HERE: https://alvinalexander.com
 *  [ I PERSONALLY DO NOT FANCY THIS CODE SO MUCH - AS THIS IS A BLOCKING OPERATION.
 * IN REAL APPLICATION WE WILL USE REACTIVE STREAMS.. ETC ].
 */
object JdbcConnector {
    var connection: Connection = null;
    val logger :Logger =  Logger(this.getClass())

    /***
     * Create a Basic Connection to mysql
     */
    def mysqlConnect(): Option[Connection] = {
        Try {
            // make the connection
            Class.forName(Constants.jdbcDriver)
            connection = DriverManager.getConnection(
                Constants.mysqlServerUrl, Constants.mysqlUserName, Constants.mysqlPassword);
            connection
        } match {
            case Success(connection) =>
                val msg = s"MySql connection successfully established."
                logger.info(msg)
                Some(connection)
            case Failure(ex) =>
                val msg = s"Failed to execute mysql queries . exception occured:${ex}}"
                ex.printStackTrace()
                logger.error(msg)
                connection.close()
                None
        }
    }

        /**
         * Init mySql Database
         */
        def initMysqlDB(): Unit = {
            Class.forName(Constants.jdbcDriver)
            val statement = connection.createStatement()
            Try {
               statement.execute(SQLQueries.dbCreate)
                statement.execute(SQLQueries.dbUse)

               statement.execute(SQLQueries.flightsTableCreate)
               statement.execute(SQLQueries.pricesTableCreate)
            } match {
                case Success(resultSet) =>
                    val msg = s"MySql db was successfully created."
                    logger.info(msg)
                case Failure(ex) =>
                    val msg = s"Failed to build mysql tables. Exception occured:${ex}}"
                    ex.printStackTrace()
                    logger.error(msg)
            }
        }

        def insertFlight(row:Seq[String]): Unit ={
            val preparedStmt: PreparedStatement = connection.prepareStatement(SQLQueries.flightsTableRowInsertQuery)
            val flightDate = row(0)
            val flightId = row(1)
            val flightFrom = row(2)
            val flightTo = row(3)
            val flightTimeStamp = row(4)
            val flightDuration = row(5)

            preparedStmt.setString (1, flightDate)
            preparedStmt.setString (2, flightId)
            preparedStmt.setString (3, flightFrom)
            preparedStmt.setString (4, flightTo)
            preparedStmt.setString (5, flightTimeStamp)
            preparedStmt.setInt (6,  flightDuration.toInt)
            preparedStmt.execute
            logger.info(s"Successfully stored flight id:${flightId} in db")
            preparedStmt.close()
        }


    def insertPrice(row:List[String]): Boolean ={

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
        preparedStmt.setString (3, seats)
        preparedStmt.setString (4, price)
        preparedStmt.execute
        print(s"Successfully stored row for prices of flight-id:${flightId} in db")
        preparedStmt.close()
        true;
    }
    }


