package scalikejdbc.stream.akka

import akka.NotUsed
import akka.stream.scaladsl.Source
import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.stream.{ ResultSetIterator, SQLToRsIterator }
import scalikejdbc.{ ConnectionPool, SQL, WithExtractor }

object SQLSource {
  def apply[A, E <: WithExtractor](sql: SQL[A, E], pool: ConnectionPool)(
    implicit
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor
  ): Source[A, NotUsed] = {
    Source.unfoldResource[A, ResultSetIterator[A]](
      () => SQLToRsIterator.toResultSetIterator(sql, pool),
      it => if (it.hasNext) Some(it.next()) else None,
      it => it.onFinish()
    )
  }
}
