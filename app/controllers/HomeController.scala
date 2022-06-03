package controllers

import db_connector.JdbcConnector

import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject._
import play.api.mvc._
import utils.{CsvReader, CsvReaderImpl}

import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents,reader:CsvReaderImpl,
                               connector:JdbcConnector) extends
  BaseController {

  /**
   * Method to refresh and recreate the mysql db + data on click.
   * @return
   */
  def refreshDatabase = Action.async { request =>

    //val nameResult: JsResult[SendEmailBody] = (request.body).validate[SendEmailBody]
    Future{
        connector.dropAndRecreateDatabase()
        reader.batchLoadFlights()
        reader.batchLoadPrices()
        Ok("Successfully refresh the current database and restored its values")
      }}

  def getPriceWithConnection(date:String,from:String,to:String) = Action {
    implicit request: Request[AnyContent] =>
        //todo: query and return.
      Ok(s"Date:${date} From:${from} To:${to}")
  }

  /**
   * Main default endpoint for the "/" get request. returns 'ok'
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    Ok("ok")
  }
}
