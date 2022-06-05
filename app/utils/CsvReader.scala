package utils

import scala.concurrent.Future


trait CsvReader{
    def batchLoadPrices():Future[Boolean];
    def batchLoadFlights():Future[Boolean];
}
