package scalikejdbc.fs2

import _root_.fs2._
import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.{ ConnectionPoolContext, DBSession, NoConnectionPoolContext, ReadOnlyAutoSession, SQL, WithExtractor }

object SQLStream {
  def apply[A, E <: WithExtractor](sql: SQL[A, E])(
    implicit
    session: DBSession = ReadOnlyAutoSession,
    context: ConnectionPoolContext = NoConnectionPoolContext,
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor,
    S: Strategy
  ): Stream[Task, A] = {

    val initIterator = Task {
      val iterator = SQLToIterator(sql).apply()
      new State(session, iterator)
    }

    Stream.bracket(initIterator)(
      s => Stream.unfoldEval(s)(s => Task(if (s.iterator.hasNext) Some((s.iterator.next(), s)) else None)),
      s => Task(s.session.close())
    )
  }

  private class State[A](val session: DBSession, val iterator: Iterator[A])

}
