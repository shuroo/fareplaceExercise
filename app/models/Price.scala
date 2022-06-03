package models

import db_connector.{ SQLQueries}
import play.api.Logger

import java.sql.PreparedStatement
import scala.util.{Failure, Success, Try}

/**
 * Case class to represent a Flight Price DB record in the ORM
 */
case class Price(flightDate:String, flightId:String, numberOfSeats:Int, price:Int)

object Price{

    val logger = Logger(this.getClass())

}
