package com.softwaremill.bootzooka.infrastructure

import io.circe.generic.AutoDerivation
import io.circe.Printer

/** Import the members of this object when doing JSON serialisation or deserialisation.
  */
object Json extends AutoDerivation {
  val noNullsPrinter: Printer = ???
}
