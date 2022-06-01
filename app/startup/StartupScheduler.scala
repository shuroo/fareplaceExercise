package startup


import db_connector.JdbcConnector
import play.api.inject.ApplicationLifecycle

import javax.inject._
import scala.concurrent.Future

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class StartupScheduler @Inject()(lifecycle: ApplicationLifecycle) {

    db_connector.JdbcConnector.mysqlConnect()
    db_connector.JdbcConnector.initMysqlDB()
    print("************************************I ran on startup!*********************************")


    // Shut-down hook
    lifecycle.addStopHook {
        () =>
            print("************************************I ran on stop!*********************************")
            JdbcConnector.closeConnection();
            Future.successful(())
    }
    //...
}
