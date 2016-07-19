package scalikejdbc.stream

import scalikejdbc.{ DBSession, SQL, SQLToResult, WithExtractor, WrappedResultSet }

trait SQLToIterator[A, E <: WithExtractor] extends SQLToResult[A, E, Iterator] {
  def result[AA](f: WrappedResultSet => AA, session: DBSession): Iterator[AA] = {
    session.collection[AA, Iterator](statement, rawParameters: _*)(f)
  }
}

object SQLToIterator {
  def apply[A, E <: WithExtractor](sql: SQL[A, E]): SQLToIterator[A, E] =
    new SQLToIteratorImpl[A, E](sql.statement, sql.rawParameters)(sql.extractor)
      .fetchSize(sql.fetchSize)
      .tags(sql.tags: _*)
      .queryTimeout(sql.queryTimeout)
}
