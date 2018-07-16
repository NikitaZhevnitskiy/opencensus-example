package ru.zhenik.opencensus.example

import com.typesafe.config.ConfigFactory


case class Configuration(microservice: Microservice)
case class Microservice(host: String, port: Int, url: String)

object Configuration {
  def load():Configuration = {
    val conf = ConfigFactory.load()
    Configuration(
      Microservice(
        conf.getString("microservice.host"),
        conf.getInt("microservice.port"),
        conf.getString("microservice.url-path")
      )
    )
  }
}
