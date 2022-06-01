package utils

import akka.actor.ActorSystem
import play.api.libs.concurrent.CustomExecutionContext

import javax.inject.Inject
import scala.concurrent.ExecutionContext

// Make sure to bind the new context class to this trait using one of the custom
// binding techniques listed on the "Scala Dependency Injection" documentation page
trait FarePlaceExCustomExecutionContext extends ExecutionContext

class FarePlaceExCustomExecutionContextImpl @Inject() (system: ActorSystem)
    extends CustomExecutionContext(system, "custom.executor for async requests")
        with FarePlaceExCustomExecutionContext
