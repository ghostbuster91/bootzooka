package com.softwaremill.bootzooka.util

import cats.effect.Sync
import com.softwaremill.tagging._
import cats.effect.std.Random
import cats.syntax.all._
import java.util.UUID

/** Any effects that are run as part of transactions and outside of transactions, need to be parametrised with the effect type. */
trait IdGenerator {
  def nextId[F[_]: Sync, U](): F[Id @@ U]
}

object DefaultIdGenerator extends IdGenerator {
  override def nextId[F[_]: Sync, U](): F[Id @@ U] = Sync[F].delay(UUID.randomUUID().taggedWith[U])
}
