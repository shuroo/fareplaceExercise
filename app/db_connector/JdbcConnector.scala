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
    val logger: Logger = Logger(this.getClass())

    /**
     * On shut down: Method to close the jdbc connection.
     * @return boolean: was the attempt succeeded or not.
     */
    def closeConnection(): Boolean ={
        Try{connection.close()} match {
            case Success(connection) =>
                val msg = s"MySql connection successfully closed."
                logger.info(msg)
                true
            case Failure(ex) =>
                val msg = s"Failed to close mysql connection . exception occured:${ex}}"
                ex.printStackTrace()
                logger.error(msg)
                connection.close()
                false
        }

    }

    /** *
     * Create a Basic Connection to mysql
     * @return Option[Connection] - the connection wrapped in some when succeeded, or None otherwise.
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
                val msg = s"Failed to connect to mysql database . exception occured:${ex}}"
                ex.printStackTrace()
                logger.error(msg)
                connection.close()
                None
        }
    }

    /**
     * Init mySql Database
     */
    def initMysqlDB(): Boolean = {
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
                true
            case Failure(ex) =>
                val msg = s"Failed to build mysql tables. Exception occured:${ex}}"
                ex.printStackTrace()
                logger.error(msg)
                false
        }
    }

    /**
     * Drop the current database and recreate all its records.
     */
    def dropAndRecreateDatabase(): Boolean ={
        Class.forName(Constants.jdbcDriver)
        val statement = connection.createStatement()
        Try {
            statement.execute(SQLQueries.dbDrop)
        } match {
            case Success(resultSet) =>
                initMysqlDB();
            case Failure(ex) =>
                val msg = s"Failed to drop mysql database. Exception occured:${ex}}"
                ex.printStackTrace()
                logger.error(msg)
                false
        }
    }

}


