/****
 * Date utilities to handle the 'Date' param in getAllConnectionWithPrice endpoint, and more.
 */
package utils
import scala.util.Try

object DateUtils {

    def isFormatted(dateString:String):Boolean = {
        dateString.charAt(4).equals('-') && dateString.charAt(7).equals('-')
    }
    /**
     * Simple aid Method to convert date param into the format / form: "yyyy-MM-dd".
     * If conversion fails, return the original date value.
     * @param dateString - the date string param
     * @return
     */
    def formatDateParam(dateString: String): String = {
        if(isFormatted(dateString)) {
            dateString
        }
        else{
            s"""${ dateString.substring(0,4) }-${ dateString.substring(4,6) }-${ dateString.substring(6,8) }"""
        }
    }

}
