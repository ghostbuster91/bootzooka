package com.softwaremill.bootzooka

import cats.effect.{IO, Resource, ResourceApp}
import com.softwaremill.bootzooka.config.Config
import com.typesafe.scalalogging.StrictLogging

object Main extends ResourceApp.Forever with StrictLogging {
  Thread.setDefaultUncaughtExceptionHandler((t, e) => logger.error("Uncaught exception in thread: " + t, e))


  val config: Config = Config.read

  /** Creating a resource which combines three resources in sequence:
    *
    *   - the first creates the object graph and allocates the dependencies
    *   - the second starts the background processes (here, an email sender)
    *   - the third allocates the http api resource
    *
    * Thanks to ResourceApp.Forever the result of the allocation is used by a non-terminating process (so that the http server is available
    * as long as our application runs).
    */
  override def run(list: List[String]): Resource[IO, Unit] = for {
    deps <- Dependencies.wire(Config.read)
    _ <- deps.httpApi.resource
  } yield ()
}
