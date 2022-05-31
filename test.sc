import com.github.tototoshi.csv.CSVReader
import db_connector.JdbcConnector
import play.api.Play
import utils.CsvReader.logger

import java.io.File
import scala.util.{Failure, Success, Try}

def batchLoadPrices(): Unit = {
    for (rows <- CSVReader.open(new File("/home/shiris/play-exercise-06-22/prices.csv")).all(); row <- rows) yield { // CSVReader.open(new File(fileName)).all()

        val line :List[String]= row.split(",").map(_.trim()).toList

        print("line:"+line)
       // println("path:"+play.api.Play..getAbsolutePath())
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
JdbcConnector.initMysqlDB();
batchLoadPrices();