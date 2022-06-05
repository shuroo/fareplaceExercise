package controllers

import db_connector.{DAL, DALImpl, JdbcConnector}
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
                               reader: CsvReader, dal: DAL) extends
    BaseController {

    /**
     * Load csv into the already existing database (tables previously created)
     * @return
     */
    def loadCSV = {
        Action.async {
            request =>
                        for(
                            flightLoadResult <- reader.batchLoadFlights();
                            priceLoadResults <- reader.batchLoadPrices())
                        yield {
                            Ok("Successfully refreshed the current database and restored its values")
                        }}
                }

    /**
     * Method to refresh and recreate the mysql db + data on click.
     *
     * @return
     */
    def refreshDatabase = {
        Action.async {
            request =>
                dal.dropAndRecreateDatabase().flatMap {
                    case true =>
                        for(
                        flightLoadResult <- reader.batchLoadFlights();
                        priceLoadResults <- reader.batchLoadPrices())
                            yield{
                                Ok("Successfully refresh the current database and restored its values")
                            }
                    case false =>
                        Future.successful(BadRequest("Failed to refresh database! Aborting"))
                }
        }
    }

    def getPriceWithConnection(date: String, from: String, to: String) = {
        Action.async {
            request =>

                // implicit val results_writer = Json.writes[JsObject]
                dal.getPriceWithConnection(date, from, to).map(_.sql_records.mkString(",")).map(Ok(_))

        }
    }

    /**
     * Sample Data to access a similar endpoint
     */
    def getPriceWithConnectionTestData(date: String, from: String, to: String): Unit ={
        Action.async {
            request =>

                // implicit val results_writer = Json.writes[JsObject]
                dal.getPriceWithConnection(date, from, to).map(_.sql_records.mkString(",")).map(Ok(_))

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
