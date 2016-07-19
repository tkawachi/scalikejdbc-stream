package scalikejdbc.stream

import java.sql.ResultSet

import scalikejdbc.{ ResultSetCursor, WrappedResultSet }

class ResultSetIterator[+A](
    rs: ResultSet,
    extractor: WrappedResultSet => A,
    val onFinish: () => Unit,
    cursor: ResultSetCursor
) extends Iterator[A] {

  def this(rs: ResultSet, extractor: WrappedResultSet => A, onFinish: () => Unit) =
    this(rs, extractor, onFinish, new ResultSetCursor(0))

  override def hasNext: Boolean = !rs.isLast

  override def next(): A = {
    rs.next()
    cursor.position += 1
    extractor(WrappedResultSet(rs, cursor, cursor.position))
  }

  def appendOnFinish(f: () => Unit): ResultSetIterator[A] = {
    val newOnFinish = () => {
      onFinish()
      f()
    }
    new ResultSetIterator(rs, extractor, newOnFinish, cursor)
  }

}

