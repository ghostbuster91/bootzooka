package com.softwaremill.bootzooka.user

import cats.MonadError
import cats.implicits._
import com.softwaremill.bootzooka._
import com.softwaremill.bootzooka.util._
import cats.effect.IO

class UserService(
    idGenerator: IdGenerator,
    userModel: UserModel[IO]
) {

  def registerNewUser(login: String, email: String, password: String): IO[User] = {
    val loginClean = login.trim()
    val emailClean = email.trim()

    def doRegister(): IO[User] = for {
      id <- idGenerator.nextId[IO, User]()
      user = User(id, loginClean, loginClean.lowerCased, emailClean.lowerCased, User.hashPassword(password))
      _ <- userModel.insert(user)
    } yield user

    for {
      _ <- UserValidator(Some(loginClean), Some(emailClean), Some(password)).as[IO]
      r <- doRegister()
    } yield r
  }

  def login(loginOrEmail: String, password: String): IO[String] = {
    IO.raiseError(new RuntimeException("todo"))
  }
}

object UserValidator {
  val MinLoginLength = 3
}

case class UserValidator(loginOpt: Option[String], emailOpt: Option[String], passwordOpt: Option[String]) {
  private val ValidationOk = Right(())

  private val emailRegex =
    """^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  val result: Either[String, Unit] = {
    for {
      _ <- validateLogin(loginOpt)
      _ <- validateEmail(emailOpt)
      _ <- validatePassword(passwordOpt)
    } yield ()
  }

  def as[F[_]](implicit me: MonadError[F, Throwable]): F[Unit] =
    result.fold(msg => Fail.IncorrectInput(msg).raiseError[F, Unit], _ => ().pure[F])

  private def validateLogin(loginOpt: Option[String]): Either[String, Unit] =
    loginOpt.map(_.trim) match {
      case Some(login) =>
        if (login.length >= UserValidator.MinLoginLength) ValidationOk else Left("Login is too short!")
      case None => ValidationOk
    }

  private def validateEmail(emailOpt: Option[String]): Either[String, Unit] =
    emailOpt.map(_.trim) match {
      case Some(email) =>
        if (emailRegex.findFirstMatchIn(email).isDefined) ValidationOk else Left("Invalid e-mail format!")
      case None => ValidationOk
    }

  private def validatePassword(passwordOpt: Option[String]): Either[String, Unit] =
    passwordOpt.map(_.trim) match {
      case Some(password) =>
        if (password.nonEmpty) ValidationOk else Left("Password cannot be empty!")
      case None => ValidationOk
    }
}
