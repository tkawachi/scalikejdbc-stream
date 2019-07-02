package scalikejdbc.stream.fs2

import _root_.fs2._
import cats.effect.ContextShift
import cats.effect.IO
import cats.syntax.apply._
import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.stream.ResultSetIterator
import scalikejdbc.stream.SQLToRsIterator
import scalikejdbc.{ ConnectionPool, SQL, WithExtractor }

object SQLStream {

  def apply[A, E <: WithExtractor](sql: SQL[A, E], pool: ConnectionPool)(
    implicit
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor,
    cs: ContextShift[IO]): Stream[IO, A] = {
    def acquire = IO.shift *> IO(SQLToRsIterator.toResultSetIterator(sql, pool))
    def release(s: ResultSetIterator[A]) = IO.shift *> IO(s.onFinish())
    Stream.bracket(acquire)(release)
      .flatMap(s => Stream.unfoldEval(s)(s => IO.shift *> IO(if (s.hasNext) Some((s.next(), s)) else None)))
  }

}
