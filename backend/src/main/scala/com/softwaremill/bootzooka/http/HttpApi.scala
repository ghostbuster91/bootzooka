package com.softwaremill.bootzooka.http

import cats.effect.IO
import cats.effect.Resource
import com.softwaremill.bootzooka.util.ServerEndpoints
import com.typesafe.scalalogging.StrictLogging
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import sttp.tapir._
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.server.model.ValuedEndpointOutput

/** Interprets the endpoint descriptions (defined using tapir) as http4s routes, adding CORS, metrics, api docs support.
  *
  * The following endpoints are exposed:
  *   - `/api/v1` - the main API
  *   - `/api/v1/docs` - swagger UI for the main API
  *   - `/admin` - admin API
  *   - `/` - serving frontend resources
  */
class HttpApi(
    http: Http,
    mainEndpoints: ServerEndpoints,
    config: HttpConfig
) extends StrictLogging {
  private val apiContextPath = List("api", "v1")

  val serverOptions: Http4sServerOptions[IO] = Http4sServerOptions
    .customiseInterceptors[IO]
    .defaultHandlers(msg => ValuedEndpointOutput(http.jsonErrorOutOutput, Error_OUT(msg)), notFoundWhenRejected = true)
    .serverLog {
      // using a context-aware logger for http logging
      Http4sServerOptions
        .defaultServerLog[IO]
    }
    .options

  lazy val routes: HttpRoutes[IO] = Http4sServerInterpreter(serverOptions).toRoutes(allEndpoints)

  lazy val allEndpoints: List[ServerEndpoint[Any, IO]] =
    mainEndpoints
      .map(se => se.prependSecurityIn(apiContextPath.foldLeft(emptyInput: EndpointInput[Unit])(_ / _)))
      .toList
      .map(_.asInstanceOf[ServerEndpoint[Any, IO]])


  /** The resource describing the HTTP server; binds when the resource is allocated. */
  lazy val resource: Resource[IO, org.http4s.server.Server] = BlazeServerBuilder[IO]
    .bindHttp(config.port, config.host)
    .withHttpApp(routes.orNotFound)
    .resource
}
