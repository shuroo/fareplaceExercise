package controllers


import javax.inject._
import play.api.mvc._
import utils.CsvReader

import scala.io.Source

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {


  /**
   *
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    db_connector.JdbcConnector.mysqlConnect()
    db_connector.JdbcConnector.initMysqlDB()
    //CsvReader.batchLoadFlights
    CsvReader.batchLoadPrices()

    Ok("ok")
  }
}
