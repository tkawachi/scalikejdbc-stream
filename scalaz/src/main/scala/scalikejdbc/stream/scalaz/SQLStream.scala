package scalikejdbc.stream.scalaz

import java.util.concurrent.ExecutorService

import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.stream.SQLToRsIterator
import scalikejdbc.{ ConnectionPool, SQL, WithExtractor }

import _root_.scalaz.concurrent.Task
import _root_.scalaz.stream._
import _root_.scalaz.stream.io.iteratorR

object SQLStream {

  def apply[A, E <: WithExtractor](sql: SQL[A, E], pool: ConnectionPool)(
    implicit
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor,
    es: ExecutorService): Process[Task, A] = {
    iteratorR(Task(SQLToRsIterator.toResultSetIterator(sql, pool)))(s => Task(s.onFinish()))(s => Task(s))
  }
}
