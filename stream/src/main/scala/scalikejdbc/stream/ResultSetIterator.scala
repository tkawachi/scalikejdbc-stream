package scalikejdbc.stream

import java.sql.ResultSet

import scalikejdbc.{ ResultSetCursor, WrappedResultSet }

class ResultSetIterator[+A](rs: ResultSet, extractor: WrappedResultSet => A, val onFinish: () => Unit) extends Iterator[A] {
  private[this] val cursor = new ResultSetCursor(0)

  override def hasNext: Boolean = !rs.isLast

  override def next(): A = {
    rs.next()
    cursor.position += 1
    extractor(WrappedResultSet(rs, cursor, cursor.position))
  }

  def appendOnFinish(f: () => Unit): ResultSetIterator[A] = {
    new ResultSetIterator(rs, extractor, { () =>
      onFinish()
      f()
    })
  }

}

