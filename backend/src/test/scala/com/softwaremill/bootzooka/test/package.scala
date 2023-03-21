package com.softwaremill.bootzooka

import com.softwaremill.bootzooka.config.Config

package object test {
  val DefaultConfig: Config = Config.read
  val TestConfig: Config = DefaultConfig
}
