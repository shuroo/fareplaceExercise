package utils
/**
 * Read and extract the csvs into mysql db.
 */
import db_connector.{DAL, JdbcConnector}
import play.api.Logger

import java.io.File
import scala.util.{Failure, Success, Try}
import com.github.tototoshi.csv._

import javax.inject.Inject

trait CsvReader{
    def batchLoadPrices():Boolean;
    def batchLoadFlights():Boolean;
}
class CsvReaderImpl @Inject()(connector:JdbcConnector,dal:DAL) extends CsvReader{

    val logger: Logger = Logger(this.getClass())


    /**
     * Method to extract all rows from csv as a set (distinct) values.
     * @param fileName - the file name required
     * @return Set[List[String]]
     */
    def extractCsvRowsAsSet(fileName:String):Set[List[String]] =CSVReader.open(new File(fileName)).all().toSet;

    /**
     * Utility method for csv
     * @return
     */
    def loadAllFlightRows(allRows:Set[List[String]])={
        allRows.map{ line=>
            Try{ dal.loadFlightRecord(line) } match {
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



    def loadAllPriceRows(allRows:Set[List[String]]): Set[Boolean] ={
        allRows.map{ line=>
            Try{ dal.loadPriceRecord(line)} match {
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

    def batchLoadFlights(): Boolean = {

        val allRows:Set[List[String]] = extractCsvRowsAsSet(Constants.pricesCSVFileName)
        // Return 'true' if a current row was successfully inserted into the db, 'false' otherwise.
        val insertStatuses = loadAllFlightRows(allRows)
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
        val insertStatuses = loadAllPriceRows(allRows)
        // Return the total status result as an 'And' operation:
        val sqlTotalInsertionsResult = insertStatuses.foldLeft(true)((acc, isInserted) => {
            acc & isInserted
        })
        sqlTotalInsertionsResult
    }
}
