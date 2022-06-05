package db_connector

import models.GetPriceWithConnectionResults

import scala.concurrent.Future


/**
 * Interface for the data access layer.
 */
trait DAL{

    def createDB(): Future[Boolean]
    def dropAndRecreateDatabase(): Future[Boolean]

    def loadFlightRecord(row:List[String]):Future[Boolean]

    def loadPriceRecord(row:List[String]):Future[Boolean]

    def getPriceWithConnection(dte:String,src:String,dst:String):Future[GetPriceWithConnectionResults]
}
