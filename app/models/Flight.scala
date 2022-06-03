package models

import play.api.Logger

/**
 * Case class to represent a Flight DB record in the ORM
 */

case class Flight(flightDate:String, flightId:String,flightFrom:String,flightTo:String,
                  flightTimeStamp:String, flightDuration:Int)

object Flight {

    val logger = Logger(this.getClass())
}
