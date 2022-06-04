/****
 * Date utilities to handle the 'Date' param in getAllConnectionWithPrice endpoint, and more.
 */
package utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

object DateUtils {

    /**
     * method for trying to handle date format . if conversion fails, return the original date value.
     * @param dateString
     * @return
     */
    def toSimpleDate(dateString: String): String = {
        val parser = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.S")
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        Try {
            LocalDateTime.parse(dateString, parser)
        }.toOption
          match{
            case Some(dte) => dte.format(formatter)
            case None => dateString
        }
    }

//    val dte = toSimpleDate("20110930 00:00:00.0") // Some("20119030")
//    toSimpleDate("Meh") // None
//
//    dte.get.toString()
}
