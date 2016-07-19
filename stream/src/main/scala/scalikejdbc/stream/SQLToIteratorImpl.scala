package scalikejdbc.stream

import scalikejdbc.{ HasExtractor, SQL, WithExtractor, WrappedResultSet }

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
