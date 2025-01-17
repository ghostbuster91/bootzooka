package com.softwaremill.bootzooka

import java.util.Locale
import cats.data.NonEmptyList
import cats.effect.IO
import com.softwaremill.tagging._
import sttp.tapir.server.ServerEndpoint
import java.util.UUID

package object util {
  type Id = UUID

  implicit class RichString(val s: String) extends AnyVal {
    def asId[T]: Id @@ T = s.asInstanceOf[Id @@ T]
    def lowerCased: String @@ LowerCased = s.toLowerCase(Locale.ENGLISH).taggedWith[LowerCased]
  }

  type ServerEndpoints = NonEmptyList[ServerEndpoint[Unit, IO]]
}
