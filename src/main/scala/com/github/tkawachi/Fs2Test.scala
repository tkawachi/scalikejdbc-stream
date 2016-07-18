package com.github.tkawachi

import fs2._
import scalikejdbc._
import scalikejdbc.fs2.SQLStream

object Fs2Test extends App {

  implicit val strategy = Strategy.fromFixedDaemonPool(3)

  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")

  println(SQLStream(sql"select 1".map(_.int(1))).take(10).runLog.unsafeAttemptRun())
}
