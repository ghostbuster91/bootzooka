package com.softwaremill.bootzooka.user

import java.time.Instant

import cats.implicits._
import com.softwaremill.bootzooka.util.{Id, LowerCased}
import tsec.common.VerificationStatus
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt
import cats.Applicative

class UserModel[F[_]: Applicative] {

  def insert(user: User): F[Unit] = {
    ().pure[F]
  }

  def findByEmail(email: String ): F[Option[User]] = {
    Option.empty[User].pure[F]
  }
  def findByLogin(login: String): F[Option[User]] = {
    Option.empty[User].pure[F]
  }
}

case class User(
    id: Id,
    login: String,
    loginLowerCased: String,
    emailLowerCased: String,
    passwordHash: PasswordHash[SCrypt],
) {

  def verifyPassword(password: String): VerificationStatus = SCrypt.checkpw[cats.Id](password, passwordHash)
}

object User {
  def hashPassword(password: String): PasswordHash[SCrypt] = SCrypt.hashpw[cats.Id](password)
}
