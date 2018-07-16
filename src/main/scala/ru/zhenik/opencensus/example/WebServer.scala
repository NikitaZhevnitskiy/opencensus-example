package ru.zhenik.opencensus.example

import java.util.Collections

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import example.akkacensus.HttpServerStatsRecorder
import io.opencensus.exporter.stats.prometheus.PrometheusStatsCollector
import io.prometheus.client.exporter.HTTPServer
import io.opencensus.stats._
import io.opencensus.stats.Measure.MeasureLong
import io.opencensus.tags.{TagContext, TagKey, Tagger, Tags}
import io.prometheus.client.Counter

import scala.concurrent.ExecutionContextExecutor
import scala.util.Random

object WebServer extends App  {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val tagger = Tags.getTagger
  private val viewManager = Stats.getViewManager
  private val statsRecorder = Stats.getStatsRecorder

  var counter = 0l
  val count = {
    counter=counter+1
    counter
  }

  // frontendKey allows us to break down the recorded data// frontendKey allows us to break down the recorded data
  val TAG = TagKey.create("my_super_tag")
  val HELLO_MEASURE_LONG = MeasureLong.create("my_super_measure_long", "amount of hello calls", "By")
  val HELLO_CALL_AMOUNT_VIEW_NAME = View.Name.create("my_super_measure_long")
  val v = View.create(
    HELLO_CALL_AMOUNT_VIEW_NAME,
    "amount of /hello calls",
    HELLO_MEASURE_LONG,
    Aggregation.Count.create(),
    Collections.singletonList(TAG))


  HttpServerStatsRecorder.register()
  val census = HttpServerStatsRecorder()
  viewManager.registerView(v)

  val rnd = new Random
  val handler: String => Route = { _ =>
    Thread.sleep(rnd.nextInt(3)*100)
    rnd.nextInt(30) match {
      case 0 =>
        println(s"response: 400")
        statsRecorder.newMeasureMap.put(HELLO_MEASURE_LONG, 1).record()
        complete(StatusCodes.BadRequest)
      case 1 =>
        println(s"response: 500")
        statsRecorder.newMeasureMap.put(HELLO_MEASURE_LONG, 1).record()
        complete(StatusCodes.InternalServerError)
      case _ =>
        println(s"response: 200")
        statsRecorder.newMeasureMap.put(HELLO_MEASURE_LONG, 1).record()
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Say hello to akka-http"))
    }
  }


  val route: Route =
    census.statsDirective {
      path("hello") {
        entity(as[String])(handler)
      } ~
        path("world") {
          entity(as[String])(handler)
        }
    }
  // conf
  val config = Configuration.load()

  // http routes
//  val http = new HttpRoute()



  // binding, return for testing
  val bindingFuture = Http().bindAndHandle(route, config.microservice.host, config.microservice.port)
//  bindingFuture

  PrometheusStatsCollector.createAndRegister()
  val promServer = new HTTPServer(7778, true)


}
