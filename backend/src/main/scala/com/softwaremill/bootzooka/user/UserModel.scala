package com.softwaremill.bootzooka.user

import cats.implicits._
import tsec.common.VerificationStatus
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt
import com.softwaremill.bootzooka.util.Id
import cats.effect.kernel.Sync

class UserModel[F[_]: Sync] {
  val db = scala.collection.mutable.HashMap[String, User]()

  def insert(user: User): F[Unit] = {
    Sync[F].delay(db.put(user.login, user)).void
  }

  def findByLogin(login: String): F[Option[User]] = {
    Sync[F].delay(db.get(login))
  }
}

case class User(
    id: Id,
    login: String,
    loginLowerCased: String,
    emailLowerCased: String,
    passwordHash: PasswordHash[SCrypt]
) {

  def verifyPassword(password: String): VerificationStatus = SCrypt.checkpw[cats.Id](password, passwordHash)
}

object User {
  def hashPassword(password: String): PasswordHash[SCrypt] = SCrypt.hashpw[cats.Id](password)
}
