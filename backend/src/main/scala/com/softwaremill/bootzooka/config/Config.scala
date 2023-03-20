package com.softwaremill.bootzooka.config

import com.softwaremill.bootzooka.user.UserConfig
import com.softwaremill.bootzooka.version.BuildInfo
import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.collection.immutable.TreeMap
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

    val info = TreeMap(BuildInfo.toMap.toSeq: _*).foldLeft(baseInfo) { case (str, (k, v)) =>
      str + s"$k: $v\n"
    }

    logger.info(info)
  }

  def read: Config = ConfigSource.default.loadOrThrow[Config]
}
