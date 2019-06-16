package scalikejdbc.stream

import scalikejdbc.GeneralizedTypeConstraintsForWithExtractor.=:=
import scalikejdbc.{ ConnectionPool, DBConnectionAttributesWiredResultSet, DBSession, LogSupport, SQL, SQLToResult, WithExtractor, WrappedResultSet }

private[stream] trait SQLToRsIterator[A, E <: WithExtractor]
  extends SQLToResult[A, E, ResultSetIterator] {

  def result[AA](f: WrappedResultSet => AA, session: DBSession): ResultSetIterator[AA] = {
    val executor = session.toStatementExecutor(statement, rawParameters)
    val proxy = new DBConnectionAttributesWiredResultSet(executor.executeQuery(), session.connectionAttributes)

    new ResultSetIterator(proxy, f, () => {
      try {
        executor.close()
      } finally {
        // see DBSession#using()
        session.fetchSize(None)
        // session.tags() // TODO clear tags to Vector.empty
        session.queryTimeout(None)
      }
    })
  }
}

private[stream] object SQLToRsIterator extends LogSupport {
  def apply[A, E <: WithExtractor](sql: SQL[A, E]): SQLToRsIterator[A, E] =
    new SQLToRsIteratorImpl[A, E](sql.statement, sql.rawParameters)(sql.extractor)
      .fetchSize(sql.fetchSize)
      .tags(sql.tags: _*)
      .queryTimeout(sql.queryTimeout)

  def toResultSetIterator[A, E <: WithExtractor](sql: SQL[A, E], pool: ConnectionPool)(
    implicit
    hasExtractor: sql.ThisSQL =:= sql.SQLWithExtractor): ResultSetIterator[A] = {
    implicit val session = DBSession(pool.borrow(), isReadOnly = true)
    val iterator = SQLToRsIterator(sql).apply()
    iterator.appendOnFinish(() => session.close())
  }
}

