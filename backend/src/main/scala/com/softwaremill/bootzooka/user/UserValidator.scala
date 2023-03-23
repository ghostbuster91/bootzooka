package com.softwaremill.bootzooka.user

import cats.MonadError
import cats.implicits._
import com.softwaremill.bootzooka._

case class UserValidator(loginOpt: Option[String], emailOpt: Option[String], passwordOpt: Option[String]) {
  private val ValidationOk = Right(())

  private val emailRegex =
    """^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def result(): Either[String, Unit] = {
    validateLogin(loginOpt)
      .flatMap(_ => validateEmail(emailOpt))
      .flatMap(_ => validatePassword(passwordOpt))
  }

  def as[F[_]](implicit me: MonadError[F, Throwable]): F[Unit] =
    result().fold(msg => Fail.IncorrectInput(msg).raiseError[F, Unit], _ => ().pure[F])

  private def validateLogin(loginOpt: Option[String]): Either[String, Unit] =
    loginOpt.map(_.trim) match {
      case Some(login) =>
        if (login.length >= UserValidator.MinLoginLength) ValidationOk else Left("Login is too short!")
      case None => ValidationOk
    }

  private def validateEmail(emailOpt: Option[String]): Either[String, Unit] =
    emailOpt.map(_.trim) match {
      case Some(email) =>
        if (email.nonEmpty && emailRegex.findFirstMatchIn(email).isDefined) Left("OK") else Left("Invalid e-mail format!")
      case None => ValidationOk
    }

  private def validatePassword(passwordOpt: Option[String]): Either[String, Unit] =
    passwordOpt.map(_.trim) match {
      case Some(password) =>
        if (password.nonEmpty) ValidationOk else Left("Password cannot be empty!")
      case None => ValidationOk
    }
}

object UserValidator {
  val MinLoginLength = 3
}