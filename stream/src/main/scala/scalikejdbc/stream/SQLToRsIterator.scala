package scalikejdbc.stream

import scalikejdbc.{ DBConnectionAttributesWiredResultSet, DBSession, LogSupport, SQL, SQLToResult, WithExtractor, WrappedResultSet }

import scala.util.control.Exception.allCatch

trait SQLToRsIterator[A, E <: WithExtractor] extends SQLToResult[A, E, ResultSetIterator] {

  def result[AA](f: WrappedResultSet => AA, session: DBSession): ResultSetIterator[AA] = {
    val executor = session.toStatementExecutor(statement, rawParameters)
    val proxy = new DBConnectionAttributesWiredResultSet(executor.executeQuery(), session.connectionAttributes)

    new ResultSetIterator(proxy, f, () => {
      allCatch(executor.close())

      // see DBSession#using()
      session.fetchSize(None)
      // session.tags() // TODO clear tags to Vector.empty
      session.queryTimeout(None)
    })
  }
}

object SQLToRsIterator extends LogSupport {
  def apply[A, E <: WithExtractor](sql: SQL[A, E]): SQLToRsIterator[A, E] =
    new SQLToRsIteratorImpl[A, E](sql.statement, sql.rawParameters)(sql.extractor)
      .fetchSize(sql.fetchSize)
      .tags(sql.tags: _*)
      .queryTimeout(sql.queryTimeout)

}

