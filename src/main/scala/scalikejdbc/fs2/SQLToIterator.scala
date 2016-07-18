package scalikejdbc.fs2

import scalikejdbc.{ DBSession, HasExtractor, SQL, SQLToResult, WithExtractor, WrappedResultSet }

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

class SQLToIteratorImpl[A, E <: WithExtractor](
  override val statement: String, private[scalikejdbc] override val rawParameters: Seq[Any]
)(
  override val extractor: WrappedResultSet => A
)
    extends SQL[A, E](statement, rawParameters)(extractor)
    with SQLToIterator[A, E] {

  override protected def withParameters(params: Seq[Any]): SQLToIterator[A, E] = {
    new SQLToIteratorImpl[A, E](statement, params)(extractor)
  }

  override protected def withStatementAndParameters(state: String, params: Seq[Any]): SQLToIterator[A, E] = {
    new SQLToIteratorImpl[A, E](state, params)(extractor)
  }

  override protected def withExtractor[B](f: WrappedResultSet => B): SQLToIterator[B, HasExtractor] = {
    new SQLToIteratorImpl[B, HasExtractor](statement, rawParameters)(f)
  }

}
