package com.softwaremill.bootzooka.test

import cats.effect.IO
import com.softwaremill.bootzooka.user.UserApi._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import sttp.client3
import sttp.client3.{Response, SttpBackend, UriContext, basicRequest}

import scala.util.Random

class Requests(backend: SttpBackend[IO, Any]) extends TestSupport {

  private val random = new Random()

  def randomLoginEmailPassword(): (String, String, String) =
    (random.nextString(12), s"user${random.nextInt(9000)}@bootzooka.com", random.nextString(12))

  private val basePath = "http://localhost:8080/api/v1"

  def registerUser(login: String, email: String, password: String): Response[Either[String, String]] = {
    basicRequest
      .post(uri"$basePath/user/register")
      .body(Register_IN(login, email, password).asJson.noSpaces)
      .send(backend)
      .unwrap
  }

  def newRegisteredUsed(): RegisteredUser = {
    val (login, email, password) = randomLoginEmailPassword()
    val apiKey = registerUser(login, email, password).body.shouldDeserializeTo[Register_OUT].apiKey
    RegisteredUser(login, email, password, apiKey)
  }

  def loginUser(loginOrEmail: String, password: String, apiKeyValidHours: Option[Int] = None): Response[Either[String, String]] = {
    basicRequest
      .post(uri"$basePath/user/login")
      .body(Login_IN(loginOrEmail, password, apiKeyValidHours).asJson.noSpaces)
      .send(backend)
      .unwrap
  }

  def getUser(apiKey: String): client3.Response[Either[String, String]] = {
    basicRequest
      .get(uri"$basePath/user")
      .header("Authorization", s"Bearer $apiKey")
      .send(backend)
      .unwrap
  }

}
