package models

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsObject, Json, Writes, __}

/**
 * Json Structure to return connections: flight numbers, path, total price - as json object.
 * For Example: for a direct flight:
 *  {"FlightNumbers":["123"],"Path":"TLV-BER","Price":"100.0"}
 */

case class GetPriceWithConnectionResults(sql_records: Seq[JsObject], is_success: Boolean, error_msg: String)

object GetPriceWithConnectionResults{
    implicit val results_reader = (__ \ "sql_records").read[Seq[JsObject]] and
                                 (__ \ "is_success").read[Boolean] and
                                  (__ \ "error_msg").readNullable[String]


    implicit val results_writer  =
        ((__ \ "sql_records").write[Seq[JsObject]] and
        (__ \ "is_success").write[Boolean] and
         (__ \ "error_msg").write[String] )((unlift(GetPriceWithConnectionResults.unapply)))

}