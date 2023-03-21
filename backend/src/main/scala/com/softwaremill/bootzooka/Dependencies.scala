package com.softwaremill.bootzooka

import cats.effect.{IO, Resource}
import com.softwaremill.bootzooka.config.Config
import com.softwaremill.bootzooka.http.{Http, HttpApi, HttpConfig}
import com.softwaremill.bootzooka.user.UserApi
import com.softwaremill.macwire.autocats.autowire
import com.softwaremill.bootzooka.util.DefaultIdGenerator

case class Dependencies(httpApi: HttpApi)

object Dependencies {
  def wire(
      config: Config
  ): Resource[IO, Dependencies] = {
    def buildHttpApi(
        http: Http,
        userApi: UserApi,
        cfg: HttpConfig
    ) = {
      new HttpApi(
        http,
        userApi.endpoints,
        cfg
      )
    }

    autowire[Dependencies](
      config.api,
      DefaultIdGenerator,
      buildHttpApi _
    )
  }
}
