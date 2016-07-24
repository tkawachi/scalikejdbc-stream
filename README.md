# scalikejdbc-stream

This library provides a way to integrate scalikejdbc to various stream libraries.
Stream libraries are useful to handle huge result set.

Currently it supports scalikejdbc 2.4.1, akka-stream 2.4.x, fs2 0.9.0-M6, scalaz-stream 0.8.2.

## scalikejdbc-stream-akka

Akka stream integration.

```scala
import scalikejdbc._
import scalikejdbc.stream.akka.SQLSource
import scala.concurrent.ExecutionContext

implicit val ec: ExecutionContext = ??? // <- for DB operation
val hugeResultQuery = sql"SELECT 1".map(_.int(1))
val src = SQLSource(hugeResultQuery, ConnectionPool()) // <- Source of akka-stream
```

## scalikejdbc-stream-fs2

fs2 integration.

```scala
import scalikejdbc._
import scalikejdbc.stream.fs2.SQLStream
import fs2.Strategy

implicit val strategy: Strategy = ??? // <- for DB operation
val hugeResultQuery = sql"SELECT 1".map(_.int(1))
val stream = SQLStream(hugeResultQuery, ConnectionPool()) // <- Stream of fs2
```

## scalikejdbc-stream-scalaz

scalaz-stream integration.

```scala
import scalikejdbc._
import scalikejdbc.stream.scalaz.SQLStream
import java.util.concurrent.ExecutorService

implicit val es: ExecutorService = ??? // for DB operations
val hugeResultQuery = sql"SELECT 1".map(_.int(1))
val stream = SQLStream(hugeResultQuery, ConnectionPool()) // <- Stream of scalaz-stream
```

## Notes

### MySQL Connector/J


> By default, ResultSets are completely retrieved and stored in memory.

> The combination of a forward-only, read-only result set, with a fetch size of Integer.MIN_VALUE serves as a signal to the driver to stream result sets row-by-row. After this, any result sets created with the statement will be retrieved row-by-row.

https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-reference-implementation-notes.html
