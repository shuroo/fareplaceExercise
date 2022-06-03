package startup


import db_connector.JdbcConnector
import play.api.inject.ApplicationLifecycle

import javax.inject._
import scala.concurrent.Future

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class StartupScheduler @Inject()(connector:JdbcConnector)(lifecycle: ApplicationLifecycle) {

    connector.dbConnect()
    connector.initMysqlDB()
    print("************************************I ran on startup!*********************************")


    // Shut-down hook
    lifecycle.addStopHook {
        () =>
            print("************************************I ran on stop!*********************************")
            connector.closeConnection();
            Future.successful(())
    }
    //...
}
