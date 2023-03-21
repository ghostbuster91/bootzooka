package com.softwaremill.bootzooka.user

import com.softwaremill.bootzooka.infrastructure.Json._
import com.softwaremill.bootzooka.test.BaseTest
import com.softwaremill.bootzooka.test.RegisteredUser
import com.softwaremill.bootzooka.test.TestDependencies
import com.softwaremill.bootzooka.test.TestSupport
import com.softwaremill.bootzooka.user.UserApi._
import org.scalatest.concurrent.Eventually
import sttp.model.StatusCode

class UserApiTest extends BaseTest with Eventually with TestDependencies with TestSupport {

  "/user/register" should "register" in {
    // given
    val (login, email, password) = requests.randomLoginEmailPassword()

    // when
    val response1 = requests.registerUser(login, email, password)

    // then
    response1.code shouldBe StatusCode.Ok
    response1.body.shouldDeserializeTo[Register_OUT].apiKey

  }

  "/user/register" should "register and ignore leading and trailing spaces" in {
    // given
    val (login, email, password) = requests.randomLoginEmailPassword()

    // when
    val response1 = requests.registerUser("   " + login + "   ", "   " + email + "   ", password)

    // then
    response1.code shouldBe StatusCode.Ok
    response1.body.shouldDeserializeTo[Register_OUT].apiKey

  }

  "/user/register" should "not register if data is invalid" in {
    // given
    val (_, email, password) = requests.randomLoginEmailPassword()

    // when
    val response1 = requests.registerUser("x", email, password) // too short

    // then
    response1.code shouldBe StatusCode.BadRequest
    response1.body.shouldDeserializeToError
  }

  "/user/register" should "not register if email is taken" in {
    // given
    val (login, email, password) = requests.randomLoginEmailPassword()

    // when
    val response1 = requests.registerUser(login + "1", email, password)
    val response2 = requests.registerUser(login + "2", email, password)

    // then
    response1.code shouldBe StatusCode.Ok
    response2.code shouldBe StatusCode.BadRequest
  }

  "/user/login" should "login the user using the login" in {
    // given
    val RegisteredUser(login, _, password, _) = requests.newRegisteredUsed()

    // when
    val response1 = requests.loginUser(login, password)

    // then
    response1.body.shouldDeserializeTo[Login_OUT]
  }

  "/user/login" should "login the user using the email" in {
    // given
    val RegisteredUser(_, email, password, _) = requests.newRegisteredUsed()

    // when
    val response1 = requests.loginUser(email, password)

    // then
    response1.body.shouldDeserializeTo[Login_OUT]
  }

  "/user/login" should "login the user using uppercase email" in {
    // given
    val RegisteredUser(_, email, password, _) = requests.newRegisteredUsed()

    // when
    val response1 = requests.loginUser(email.toUpperCase, password)

    // then
    response1.body.shouldDeserializeTo[Login_OUT]
  }

  "/user/login" should "login the user with leading or trailing spaces" in {
    // given
    val RegisteredUser(login, _, password, _) = requests.newRegisteredUsed()

    // when
    val response1 = requests.loginUser("   " + login + "   ", password)

    // then
    response1.body.shouldDeserializeTo[Login_OUT]
  }

  "/user/login" should "respond with 403 HTTP status code and 'Incorrect login/email or password' message if user was not found" in {
    // given
    val RegisteredUser(_, _, password, _) = requests.newRegisteredUsed()

    // when
    val response = requests.loginUser("unknownLogin", password, Some(3))
    response.code shouldBe StatusCode.Unauthorized
    response.body.shouldDeserializeToError shouldBe "Incorrect login/email or password"
  }

  "/user/login" should "respond with 403 HTTP status code and 'Incorrect login/email or password' message if password is incorrect for user" in {
    // given
    val RegisteredUser(login, _, _, _) = requests.newRegisteredUsed()

    // when
    val response = requests.loginUser(login, "wrongPassword", Some(3))
    response.code shouldBe StatusCode.Unauthorized
    response.body.shouldDeserializeToError shouldBe "Incorrect login/email or password"
  }

  "/user/info" should "respond with 403 if the token is invalid" in {
    requests.getUser("invalid").code shouldBe StatusCode.Unauthorized
  }

}
