package utils
/**
 * Read and extract the csvs into mysql db.
 */
import db_connector.JdbcConnector
import play.api.Logger

import scala.io.Source
import java.io.File
import java.sql.Connection
import scala.util.{Failure, Success, Try}
import com.github.tototoshi.csv._

import scala.concurrent.Future
object CsvReader {

    val logger: Logger = Logger(this.getClass())

    def batchLoadFlights(): Unit = {
        for (rows <- scala.io.Source.fromFile(Constants.flightsCSVFileName).getLines(); row <- rows) { // CSVReader.open(new File(fileName)).all()

            val line = row.toString.split(",").map(_.trim)

            Try{ JdbcConnector.insertFlight(line) } match {
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

    def batchLoadPrices(): Unit = {
        val allRows = CSVReader.open(new File(Constants.pricesCSVFileName)).all();
        allRows.map{ line=> // CSVReader.open(new File(fileName))
            // .all()

          //  val line :List[String]= row.split(",").map(_.trim()).toList

            print("line:"+line)
            Try{ JdbcConnector.insertPrice(line)} match {
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
