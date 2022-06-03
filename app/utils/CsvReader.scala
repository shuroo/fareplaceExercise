package utils
/**
 * Read and extract the csvs into mysql db.
 */
import db_connector.{JdbcConnector, SQLQueries}
import play.api.Logger

import java.io.File
import java.sql.{PreparedStatement}
import scala.util.{Failure, Success, Try}
import com.github.tototoshi.csv._
import javax.inject.Inject

trait CsvReader{
    def batchLoadPrices():Boolean;
    def batchLoadFlights():Boolean;
}
class CsvReaderImpl @Inject()(connector:JdbcConnector) extends CsvReader{

    val logger: Logger = Logger(this.getClass())


    /**
     * Method to extract all rows from csv as a set (distinct) values.
     * @param fileName - the file name required
     * @return Set[List[String]]
     */
    def extractCsvRowsAsSet(fileName:String):Set[List[String]] =CSVReader.open(new File(fileName)).all().toSet;


    def loadFlightRecord(row: Seq[String]): Boolean = {
        val preparedStmt: PreparedStatement = connector.connection.prepareStatement(SQLQueries.flightsTableRowInsertQuery)
        if(row.length < 6){
            logger.error(s"Detected an invalid row of length:${row.length}- abort sending it to the db ");
            false
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
        preparedStmt.execute
        logger.info(s"Successfully stored flight id:${flightId} in db")
        preparedStmt.close()
        true
    }
    /**
     * Utility method for csv
     * @return
     */
    def loadAllFlightRows(allRows:Set[List[String]])={
        allRows.map{ line=>
            Try{ loadFlightRecord(line) } match {
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


    def loadPriceRecord(row:List[String]): Boolean ={

        if(row.length < 4){
            logger.error(s"Detected an invalid row of length:${row.length}- aborting ");
            false;
        }
        val preparedStmt: PreparedStatement = connector.connection.prepareStatement(SQLQueries.pricesTableRowInsertQuery)
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
