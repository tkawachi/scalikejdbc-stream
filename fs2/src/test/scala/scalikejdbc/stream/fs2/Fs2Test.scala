package scalikejdbc.stream.fs2

import _root_.fs2._
import org.scalatest.{ BeforeAndAfterAll, FunSuite }
import scalikejdbc._

class Fs2Test extends FunSuite with BeforeAndAfterAll {

  implicit val strategy = Strategy.fromCachedDaemonPool()

  override def beforeAll(): Unit = {
    super.beforeAll()
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")
  }

  override def afterAll(): Unit = {
    ConnectionPool.closeAll()
    super.afterAll()
  }

  test("stream") {
    val pool = ConnectionPool()
    val src = SQLStream(sql"select * FROM generate_series(0,10000)".fetchSize(10).map(_.long(1)), pool)
    assert(src.take(100).fold(0L)(_ + _).runLog.unsafeAttemptRun() == Right(Vector(4950L)))
  }
}
