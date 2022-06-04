i
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try

def toSimpleDate(dateString: String): Option[String] = {
    val parser = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.S")
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Try {
        LocalDateTime.parse(dateString, parser)
    }.toOption
        .map(_.format(formatter))
}

val dte = toSimpleDate("20110930 00:00:00.0") // Some("20119030")
toSimpleDate("Meh") // None

dte.get.toString()