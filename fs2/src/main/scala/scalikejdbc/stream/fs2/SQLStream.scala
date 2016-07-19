package scalikejdbc.stream.fs2

import _root_.fs2._
import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.stream.SQLToIterator
import scalikejdbc.{ ConnectionPool, DBSession, SQL, WithExtractor }

object SQLStream {

  def apply[A, E <: WithExtractor](sql: SQL[A, E], pool: ConnectionPool)(
    implicit
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor,
    S: Strategy
  ): Stream[Task, A] = {

    val initState: Task[State[A]] = Task {
      implicit val session = DBSession(pool.borrow(), isReadOnly = true)
      val iterator = SQLToIterator(sql).apply()
      new State(session, iterator)
    }

    Stream.bracket(initState)(
      s => Stream.unfoldEval(s)(s => Task(if (s.iterator.hasNext) Some((s.iterator.next(), s)) else None)),
      s => Task(s.session.close())
    )
  }

  private class State[A](val session: DBSession, val iterator: Iterator[A])

}
