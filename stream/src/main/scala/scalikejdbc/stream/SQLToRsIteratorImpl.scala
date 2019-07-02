package scalikejdbc.stream

import scalikejdbc.{ HasExtractor, SQL, WithExtractor, WrappedResultSet }

private[stream] class SQLToRsIteratorImpl[A, E <: WithExtractor](
  override val statement: String, private[scalikejdbc] override val rawParameters: Seq[Any])(
  override val extractor: WrappedResultSet => A)
  extends SQL[A, E](statement, rawParameters)(extractor)
  with SQLToRsIterator[A, E] {

  override protected def withParameters(params: Seq[Any]): SQLToRsIterator[A, E] = {
    new SQLToRsIteratorImpl[A, E](statement, params)(extractor)
  }

  override protected def withStatementAndParameters(state: String, params: Seq[Any]): SQLToRsIterator[A, E] = {
    new SQLToRsIteratorImpl[A, E](state, params)(extractor)
  }

  override protected def withExtractor[B](f: WrappedResultSet => B): SQLToRsIterator[B, HasExtractor] = {
    new SQLToRsIteratorImpl[B, HasExtractor](statement, rawParameters)(f)
  }

}
