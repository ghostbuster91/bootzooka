package com.softwaremill.bootzooka.user

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UserValidatorSpec extends AnyFlatSpec with Matchers {
  private def validate(userName: String, email: String, password: String) = {
    val v = UserValidator(Some(userName), Some(email), Some(password))
    v.result("kas  ")
  }

  "validate" should "not accept login containing only empty spaces" in {
    val dataIsValid = validate("   ", "admin@bootzooka.com", "password")

    dataIsValid.isLeft shouldBe true
  }

  "validate" should "not accept too short login" in {
    val tooShortLogin = "a" * (UserValidator.MinLoginLength - 1)
    val dataIsValid = validate(tooShortLogin, "admin@bootzooka.com", "password")

    dataIsValid.isLeft shouldBe true
  }

}
