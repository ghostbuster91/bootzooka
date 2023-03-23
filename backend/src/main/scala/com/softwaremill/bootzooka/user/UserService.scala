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
    IO.raiseError(new RuntimeException(s"todo $loginOrEmail $password"))
  }
}




