package com.softwaremill.bootzooka.config

import com.softwaremill.bootzooka.user.UserConfig
import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import com.softwaremill.bootzooka.http.HttpConfig

/** Maps to the `application.conf` file. Configuration for all modules of the application. */
case class Config(api: HttpConfig, user: UserConfig)

object Config extends StrictLogging {
  def log(config: Config): Unit = {
    val baseInfo = s"""
                      |Bootzooka configuration:
                      |-----------------------
                      |API:            ${config.api}
                      |User:           ${config.user}
                      |
                      |Build & env info:
                      |-----------------
                      |""".stripMargin

    logger.info(baseInfo)
  }

  def read: Config = ConfigSource.default.loadOrThrow[Config]
}
