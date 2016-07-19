package scalikejdbc.stream.fs2

import _root_.fs2._
import org.scalatest.{ BeforeAndAfterAll, FunSuite }
import scalikejdbc._

class SQLStreamTest extends FunSuite with BeforeAndAfterAll {

  implicit val strategy = Strategy.fromCachedDaemonPool()

  override def beforeAll(): Unit = {
    super.beforeAll()
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:file:./target/fs2_sql_stream_test", "user", "pass")

    DB.localTx { implicit session =>
      sql"DROP TABLE IF EXISTS FOO".execute().apply()
      sql"CREATE TABLE FOO(T TEXT)".execute().apply()

      val text = "a" * 1024 * 1024
      sql"INSERT INTO FOO SELECT $text FROM GENERATE_SERIES(0, 1024)".execute().apply()
    }
  }

  override def afterAll(): Unit = {
    ConnectionPool.closeAll()
    super.afterAll()
  }

  test("stream") {
    val pool = ConnectionPool()

    val src = SQLStream(sql"select T FROM FOO".map(_.string(1)), pool)
    val totalLength = src.take(100).fold(0L)((acc, tpl) => acc + tpl.length)
    val result = totalLength.runLog.unsafeAttemptRun()
    result.left.foreach(_.printStackTrace())

    assert(result == Right(Vector(1024 * 1024 * 100)))
  }
}
