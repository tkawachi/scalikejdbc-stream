package scalikejdbc.stream.akka

import akka.{ Done, NotUsed }
import akka.stream.scaladsl.Source
import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.stream.{ ResultSetIterator, SQLToRsIterator }
import scalikejdbc.{ ConnectionPool, SQL, WithExtractor }

import scala.concurrent.{ ExecutionContext, Future }

object SQLSource {
  def apply[A, E <: WithExtractor](sql: SQL[A, E], pool: ConnectionPool)(
    implicit
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor,
    ec: ExecutionContext): Source[A, NotUsed] = {
    Source.unfoldResourceAsync[A, ResultSetIterator[A]](
      () => Future(SQLToRsIterator.toResultSetIterator(sql, pool)),
      it => Future(if (it.hasNext) Some(it.next()) else None),
      it => Future { it.onFinish(); Done })
  }
}
