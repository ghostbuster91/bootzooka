package com.softwaremill.bootzooka.user

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class UserValidatorSpec extends AnyFunSuite with Matchers {
  private def validate(userName: String, email: String, password: String) =
    UserValidator(Some(userName), Some(email), Some(password)).result

  test("accept valid data") {
    val dataIsValid = validate("login", "admin@bootzooka.com", "password")

    dataIsValid shouldBe Right(())
  }

  test("not accept login containing only empty spaces") {
    val dataIsValid = validate("   ", "admin@bootzooka.com", "password")

    dataIsValid.isLeft shouldBe true
  }

  test("not accept too short login") {
    val tooShortLogin = "a" * (UserValidator.MinLoginLength - 1)
    val dataIsValid = validate(tooShortLogin, "admin@bootzooka.com", "password")

    dataIsValid.isLeft shouldBe true
  }

  test("not accept too short login after trimming") {
    val loginTooShortAfterTrim = "a" * (UserValidator.MinLoginLength - 1) + "   "
    val dataIsValid = validate(loginTooShortAfterTrim, "admin@bootzooka.com", "password")

    dataIsValid.isLeft shouldBe true
  }

  test("not accept missing email with spaces only") {
    val dataIsValid = validate("login", "   ", "password")

    dataIsValid.isLeft shouldBe true
  }

  test("not accept invalid email") {
    val dataIsValid = validate("login", "invalidEmail", "password")

    dataIsValid.isLeft shouldBe true
  }

  test("not accept password with empty spaces only") {
    val dataIsValid = validate("login", "admin@bootzooka.com", "    ")

    dataIsValid.isLeft shouldBe true
  }
}
