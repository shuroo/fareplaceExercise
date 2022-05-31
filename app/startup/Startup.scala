package startup


import play.api.inject.ApplicationLifecycle

import javax.inject._
import scala.concurrent.Future

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class Startup @Inject() (lifecycle: ApplicationLifecycle) {

//    db_connector.JdbcConnector.mysqlConnect()
//    db_connector.JdbcConnector.initMysqlDB()
//    print("************************************I ran on startup!")


    // Shut-down hook
    lifecycle.addStopHook {
        () =>
            print("on stop!!!")
            Future.successful(())
    }
    //...
}
