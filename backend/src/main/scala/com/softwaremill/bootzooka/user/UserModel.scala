package com.softwaremill.bootzooka.user

import cats.implicits._
import com.softwaremill.bootzooka.util.Id
import cats.effect.kernel.Sync
import java.security.MessageDigest
import java.util.Base64

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
    passwordHash: String
) {

  def verifyPassword(password: String): Boolean =
    User.hashPassword(password) == passwordHash
}

object User {
  def digest = MessageDigest.getInstance("SHA-256");
  def hashPassword(password: String): String = Base64.getEncoder().encodeToString(User.digest.digest(password.getBytes()))
}
