package db_connector

import java.sql.{Connection, DriverManager, PreparedStatement, Statement}
import scala.util.{Failure, Success, Try}
import utils.Constants
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
/**
 * A Scala JDBC connection example, based on the example given HERE: https://alvinalexander.com
 *  [ I PERSONALLY DO NOT FANCY THIS CODE SO MUCH - AS THIS IS A BLOCKING OPERATION.
 * IN REAL APPLICATION WE WILL USE REACTIVE STREAMS.. ETC ].
 */
class JdbcConnector  @Inject()(){
    var connection: Connection = null;
    val logger: Logger = Logger(this.getClass())

    /**
     * Create statement for table and view (-metadata) manipulation queries (like create,drop, etc).
     * @return
     */
    def createStatement():Statement = {
        connection.createStatement()
    }
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
    def dbConnect(): Option[Connection] = {
        Try {
            // make the connection
            Class.forName(Constants.jdbcDriver)
            connection = DriverManager.getConnection(Constants.dbName)
            connection
        } match {
            case Success(connection) =>
                val msg = s"Sql connection successfully established."
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

}


