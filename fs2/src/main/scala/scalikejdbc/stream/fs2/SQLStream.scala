package scalikejdbc.stream.fs2

import _root_.fs2._
import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.stream.SQLToRsIterator
import scalikejdbc.{ ConnectionPool, SQL, WithExtractor }

object SQLStream {

  def apply[A, E <: WithExtractor](sql: SQL[A, E], pool: ConnectionPool)(
    implicit
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor,
    S: Strategy): Stream[Task, A] = {
    Stream.bracket(Task(SQLToRsIterator.toResultSetIterator(sql, pool)))(
      s => Stream.unfoldEval(s)(s => Task(if (s.hasNext) Some((s.next(), s)) else None)),
      s => Task(s.onFinish()))
  }

}
