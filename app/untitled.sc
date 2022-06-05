i
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.util.Try
import java.text.SimpleDateFormat
import java.util.Calendar

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.TimeZone
//
//// ISO 8601 BASIC is used by the API signature
//var ISO_8601BASIC_DATE_PATTERN = "yyyyMMdd'T'HHmmss'Z'"
//
//def isIsoTimestamp(s: String) = s.matches("\\d{8}T\\d{6}Z")
//def parseIsoDateTime(s: String) = {
//    val dateFormat = new SimpleDateFormat(ISO_8601BASIC_DATE_PATTERN)
//    dateFormat.setLenient(false)
//    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
//    val result = dateFormat.parse(s, new ParsePosition(0))
//    result
//}
def calcLandingTime()={

    val myTime = "2022-12-26 06:35Z"
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    val ISO_8601BASIC_DATE_PATTERN = "yyyyMMdd'T'HHmmss'Z'"
    def isIsoTimestamp(s: String) = s.matches("\\d{8}T\\d{6}Z")
    val dateFormat = new SimpleDateFormat(ISO_8601BASIC_DATE_PATTERN)
    dateFormat.setLenient(false)
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
    val result = dateFormat.parse(s, new ParsePosition(0))
    result
    val d = df.parse(myTime)
    val cal = Calendar.getInstance
    cal.setTime(d)
    cal.add(Calendar.MINUTE, 130)
    val newTime = df.format(cal.getTime)
    newTime
}

print("lt:"+calcLandingTime())


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