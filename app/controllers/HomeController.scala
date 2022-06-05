package controllers

import db_connector.{DAL, DALImpl, JdbcConnector}
import models.GetPriceWithConnectionResults

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject._
import play.api.mvc._
import utils.CsvReader

import scala.concurrent.Future
import play.api.libs.json._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,
                               reader: CsvReader, dal: DAL) extends
    BaseController {

    /**
     * Load csv into the already existing database (tables previously created)
     * @return
     */
    def loadCSV:Action[AnyContent] = {
        Action.async{ request =>
                        for(
                            flightLoadResult <- reader.batchLoadFlights();
                            priceLoadResults <- reader.batchLoadPrices())
                        yield {
                            Ok("Successfully refreshed the current database and restored its values")
                        }}
                }

    /**
     * Main method for 'getPriceWithConnection' endpoint.
     * @param date - The requested flight date;
     * @param from - The departure airport required;
     * @param to - the Arrival airport required
     * @return Action[AnyContent] - response status 200 (OK) - sequence of json results
     */
    def getPriceWithConnection(date: String, from: String, to: String):Action[AnyContent] =
        Action.async {
            request =>
                dal.getPriceWithConnection(date, from, to) map {
                    result: GetPriceWithConnectionResults => result.is_success match {
                        case true =>
                            // Better to return as json with writers and not as a string.
                            Ok(result.sql_records.mkString(","))

                        case false =>
                            BadRequest(Json.obj("msg" -> result.error_msg))
                    }
                }
        }


    /**
     * Main default endpoint for the "/" get request. returns 'ok'
     */
    def index() = {
        Action {
            implicit request: Request[AnyContent] =>

                Ok("ok")
        }
    }
}
