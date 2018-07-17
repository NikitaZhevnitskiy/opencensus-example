package ru.zhenik.opencensus.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import io.opencensus.exporter.stats.prometheus.PrometheusStatsCollector
import io.prometheus.client.exporter.HTTPServer
import scala.concurrent.ExecutionContextExecutor

object WebServer extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  // conf
  val config = Configuration.load()
  // binding, return for testing
  val bindingFuture = Http().bindAndHandle(new HttpRoutes().route, config.microservice.host, config.microservice.port)

  PrometheusStatsCollector.createAndRegister()
  val promServer = new HTTPServer(7778, true)

}
