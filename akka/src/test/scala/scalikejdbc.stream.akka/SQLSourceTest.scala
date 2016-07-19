package scalikejdbc.stream.akka

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, Materializer }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{ Minutes, Span }
import org.scalatest.{ BeforeAndAfterAll, FunSuite }
import scalikejdbc._

class SQLSourceTest extends FunSuite with BeforeAndAfterAll with ScalaFutures {

  implicit var actorSystem: ActorSystem = _

  implicit def materializer: Materializer = ActorMaterializer()

  override implicit def patienceConfig = PatienceConfig(timeout = Span(1, Minutes))

  override def beforeAll(): Unit = {
    super.beforeAll()
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:file:./target/akka_sql_stream_test", "user", "pass")

    DB.localTx { implicit session =>
      sql"DROP TABLE IF EXISTS FOO".execute().apply()
      sql"CREATE TABLE FOO(T TEXT)".execute().apply()

      val text = "a" * 1024 * 1024
      sql"INSERT INTO FOO SELECT $text FROM GENERATE_SERIES(0, 1024)".execute().apply()
    }

    actorSystem = ActorSystem()
  }

  override def afterAll(): Unit = {
    actorSystem.terminate()
    ConnectionPool.closeAll()
    super.afterAll()
  }

  test("stream") {
    val pool = ConnectionPool()

    val src = SQLSource(sql"select T FROM FOO".map(_.string(1)), pool)
    val totalLength = src.take(100).runFold(0L)((acc, tpl) => acc + tpl.length)

    assert(totalLength.futureValue == 1024 * 1024 * 100)
  }
}
