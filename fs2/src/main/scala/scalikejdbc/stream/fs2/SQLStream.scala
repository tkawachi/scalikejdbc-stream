package scalikejdbc.stream.fs2

import _root_.fs2._
import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.stream.{ ResultSetIterator, SQLToRsIterator }
import scalikejdbc.{ ConnectionPool, DBSession, SQL, WithExtractor }

object SQLStream {

  def apply[A, E <: WithExtractor](sql: SQL[A, E], pool: ConnectionPool)(
    implicit
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor,
    S: Strategy
  ): Stream[Task, A] = {

    val initState: Task[ResultSetIterator[A]] = Task {
      implicit val session = DBSession(pool.borrow(), isReadOnly = true)
      val iterator = SQLToRsIterator(sql).apply()
      iterator.withOnFinish { () =>
        iterator.onFinish()
        session.close()
      }
    }

    Stream.bracket(initState)(
      s => Stream.unfoldEval(s)(s => Task(if (s.hasNext) Some((s.next(), s)) else None)),
      s => Task(s.onFinish())
    )
  }

}
