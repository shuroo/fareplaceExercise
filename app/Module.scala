import com.google.inject.AbstractModule
import play.libs.akka.AkkaGuiceSupport
import startup._


class Module extends AbstractModule with AkkaGuiceSupport{

  override def configure() = {

    Seq(
      //Startup Application
      bind(classOf[StartupScheduler]).asEagerSingleton()
    )

  }



}
