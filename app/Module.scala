import com.google.inject.AbstractModule
import db_connector.{DAL, DALImpl, JdbcConnector}
import play.api.libs.concurrent.CustomExecutionContext
import play.libs.akka.AkkaGuiceSupport
import play.libs.concurrent.CustomExecutionContext
import startup._
import utils.{CsvReader, CsvReaderImpl}


class Module extends AbstractModule with AkkaGuiceSupport{

  override def configure() = {

    Seq(
      //Startup Application
      bind(classOf[StartupScheduler]).asEagerSingleton(),
      bind(classOf[CsvReader]).to(classOf[CsvReaderImpl]).asEagerSingleton(),
      bind(classOf[DAL]).to(classOf[DALImpl]).asEagerSingleton(),

     // bind(classOf[JdbcConnector]).to(classOf[JdbcConnector]).asEagerSingleton()



    )

  }



}
