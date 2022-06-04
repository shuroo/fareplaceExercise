package utils

package utils

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.Logger
import play.api.mvc.Results.{BadRequest, InternalServerError}

import java.sql.SQLException
import scala.concurrent.Future
import scala.util.control.NonFatal

object ErrorHandling {


    val logger: Logger = Logger(this.getClass())

    def formatErrorMessage(message: String, details: JsValue): JsObject = {
        Json.obj("status" -> "ERROR", "message" -> message, "details" -> details)
    }

    def formatErrorMessage(message: JsValue): JsObject = {
        Json.obj("status" -> "ERROR", "message" -> message)
    }

    def formatErrorMessage(message: String): JsObject = {
        formatErrorMessage(Json.toJson(message))
    }

    /**
     * Generic Method to handle recover code.
     * Default implementation: Cache the thrown exception and add appropriate message to the user.
     *
     * @return BadRequest with the thrown exception.
     *
     *         (To be overridden on demand).
     * @param e - The Type of Exception thrown.
     * @return
     */

    def onRecover(e: Throwable) = {
        e match {
            case NonFatal(e) => InternalServerError(formatErrorMessage(e.getMessage()))
            case e: Throwable =>
                //We wish to cache exceptions here...
                logger.error(e.getMessage)
                val error_msg = s"Error occurred during execution." +
                                s"Error message: ${e.getMessage}\n"
                Future.failed(new Exception(error_msg, e))
                BadRequest(error_msg)
        }
    }


    /**
     * Throw exception on fatal cases.
     *
     * @param e
     * @return
     */
    def handleRecover(e: Throwable, context: String): Nothing = {
        e match {

            case ex: SQLException =>
                val msg = s"Failed to perform DB operation, in methon:${context}. Exception thrown."
                logger.error(msg)
                throw new Exception(msg, e)
            case _ =>
                val msg = s"""Error while trying to access method:${context}. error_message: ${e.getMessage}"""
                logger.error(msg)
                throw new Exception(msg, e)

        }
    }

}
