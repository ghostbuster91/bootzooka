package com.softwaremill.bootzooka.user

import cats.data.NonEmptyList
import cats.effect.IO
import com.softwaremill.bootzooka.http.Http
import com.softwaremill.bootzooka.infrastructure.Json._
import com.softwaremill.bootzooka.util.ServerEndpoints

import java.time.Instant
import sttp.tapir.server.ServerEndpoint

class UserApi(http: Http, userService: UserService) {
  import UserApi._
  import http._

  private val UserPath = "user"

  private val registerUserEndpoint =
    baseEndpoint.post
      .in(UserPath / "register")
      .in(jsonBody[Register_IN])
      .out(jsonBody[Register_OUT])
      .serverLogic[IO]{ data =>
        (for {
          _ <- userService.registerNewUser(data.login, data.email, data.password)
        } yield Register_OUT("")).toOut
      }

  private val loginEndpoint: ServerEndpoint[Unit, IO] = baseEndpoint.post
    .in(UserPath / "login")
    .in(jsonBody[Login_IN])
    .out(jsonBody[Login_OUT])
    .serverLogic[IO] { data =>
      (for {
        apiKey <- userService
          .login(data.loginOrEmail, data.password)
      } yield Login_OUT("")).toOut
    }

  val endpoints: ServerEndpoints =
    NonEmptyList
      .of(
        registerUserEndpoint,
        loginEndpoint
      )

}

object UserApi {
  case class Register_IN(login: String, email: String, password: String)
  case class Register_OUT(apiKey: String)

  case class Login_IN(loginOrEmail: String, password: String, apiKeyValidHours: Option[Int])
  case class Login_OUT(apiKey: String)
}
