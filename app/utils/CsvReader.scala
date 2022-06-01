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
import models.{Flight, Price}
object CsvReader {

    val logger: Logger = Logger(this.getClass())


    /**
     * Method to extract all rows from csv as a set (distinct) values.
     * @param fileName - the file name required
     * @return Set[List[String]]
     */
    def extractCsvRowsAsSet(fileName:String):Set[List[String]] =CSVReader.open(new File(fileName)).all().toSet;


    def batchLoadFlights(): Boolean = {

        val allRows:Set[List[String]] = extractCsvRowsAsSet(Constants.pricesCSVFileName)
        // Return 'true' if a current row was successfully inserted into the db, 'false' otherwise.
        val insertStatuses = Flight.loadAllFlightRows(allRows)
        // Return the total status result as an 'And' operation:
        val sqlTotalInsertionsResult = insertStatuses.foldLeft(true)((acc, isInserted) => {
            acc & isInserted
        })
        sqlTotalInsertionsResult
    }

    /**
     * Method to batch load from prices.csv into the sql prices table.
     * @return Boolean - True when ALL records successfully inserted.
     */
    def batchLoadPrices(): Boolean = {
        // Create a set of all (Distinct) csv records
        val allRows:Set[List[String]] = extractCsvRowsAsSet(Constants.pricesCSVFileName)
        // Return 'true' if a current row was successfully inserted into the db, 'false' otherwise.
        val insertStatuses = Price.loadAllPriceRows(allRows)
        // Return the total status result as an 'And' operation:
        val sqlTotalInsertionsResult = insertStatuses.foldLeft(true)((acc, isInserted) => {
            acc & isInserted
        })
        sqlTotalInsertionsResult
    }
}
