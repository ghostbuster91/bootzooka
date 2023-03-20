package com.softwaremill.bootzooka.infrastructure

import io.circe.Encoder
import io.circe.Printer
import io.circe.generic.AutoDerivation
import tsec.passwordhashers.PasswordHash
import tsec.passwordhashers.jca.SCrypt

/** Import the members of this object when doing JSON serialisation or deserialisation.
  */
object Json extends AutoDerivation {
  val noNullsPrinter: Printer = Printer.noSpaces.copy(dropNullValues = true)

  implicit val passwordHashEncoder: Encoder[PasswordHash[SCrypt]] =
    Encoder.encodeString.asInstanceOf[Encoder[PasswordHash[SCrypt]]]

}
