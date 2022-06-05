package controllers

import db_connector.{DAL, DALImpl, JdbcConnector}
import models.GetPriceWithConnectionResults
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{Json}

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject._
import play.api.mvc._
import utils.{CsvReader}

import scala.concurrent.Future
import play.api.libs.json._
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               reader:CsvReader,dal:DAL,
                               connector:JdbcConnector) extends
  BaseController {

//    implicit val results_reader = (__ \ "sql_records").read[Seq[JsObject]] and
//                                  (__ \ "is_success").readNullable[Boolean] and
//                                  (__ \ "error_msg").readNullable[String]
//
//
//    implicit val results_writer = Json.writes[GetPriceWithConnectionResults]

  /**
   * Method to refresh and recreate the mysql db + data on click.
   * @return
   */
  def refreshDatabase = Action.async { request =>

    Future{
        connector.dropAndRecreateDatabase()
        reader.batchLoadFlights()
        reader.batchLoadPrices()
        Ok("Successfully refresh the current database and restored its values")
      }}

  def getPriceWithConnection(date:String,from:String,to:String) = Action.async { request =>

      implicit val results_writer = Json.writes[JsObject]
      dal.getPriceWithConnection(date,from,to).map(_.sql_records.mkString(",")).map(Ok(_))
           // seq[future] => future[seq]
//           dal.getPriceWithConnection(date,from,to).map(recordsWrapper=>Future.
//               sequence(recordsWrapper.sql_records)).map(results=>Ok(_))

       }



/**
   * Main default endpoint for the "/" get request. returns 'ok'
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    Ok("ok")
  }
}
