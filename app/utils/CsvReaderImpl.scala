package utils
/**
 * Read and extract the csvs into mysql db.
 */
import db_connector.{DAL, JdbcConnector}
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import java.io.File
import scala.util.{Failure, Success, Try}
import com.github.tototoshi.csv._

import javax.inject.Inject
import scala.concurrent.Future

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
    def loadAllFlightRows(allRows:Set[List[String]]):Future[Set[Boolean]]={
        val storingRowsResults = allRows.map{ line=>
            dal.loadFlightRecord(line)
        }
        Future.sequence(storingRowsResults)
    }



    def loadAllPriceRows(allRows:Set[List[String]]): Future[Set[Boolean]] ={
        val storingRowsResults = allRows.map{ line=>
            dal.loadPriceRecord(line)
        }
        Future.sequence(storingRowsResults)
    }

    def batchLoadFlights(): Future[Boolean] = {

        val allRows:Set[List[String]] = extractCsvRowsAsSet(Constants.pricesCSVFileName)
        // Return 'true' if a current row was successfully inserted into the db, 'false' otherwise.
        val insertStatuses = loadAllFlightRows(allRows)
        // Return the total status result as an 'And' operation:
        val sqlTotalInsertionsResult = insertStatuses.map(_.foldLeft(true)((acc, isInserted) => {
            acc & isInserted
        }))
        sqlTotalInsertionsResult
    }

    /**
     * Method to batch load from prices.csv into the sql prices table.
     * @return Boolean - True when ALL records successfully inserted.
     */
    def batchLoadPrices(): Future[Boolean] = {
        // Create a set of all (Distinct) csv records
        val allRows:Set[List[String]] = extractCsvRowsAsSet(Constants.pricesCSVFileName)
        // Return 'true' if a current row was successfully inserted into the db, 'false' otherwise.
        val insertStatuses = loadAllPriceRows(allRows)
        // Return the total status result as an 'And' operation:
        val sqlTotalInsertionsResult = insertStatuses.map(_.foldLeft(true)((acc, isInserted) => {
            acc & isInserted
        }))
        sqlTotalInsertionsResult
    }
}
