package ru.zhenik.opencensus.example

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, path, _}
import akka.http.scaladsl.server.Route
import ru.zhenik.opencensus.example.akkacensus.{AppCustomStatsRecorder, HttpServerStatsRecorder}

import scala.concurrent.ExecutionContext

class HttpRoutes(implicit executionContext: ExecutionContext) {

  HttpServerStatsRecorder.register()
  val census = HttpServerStatsRecorder()

  val route: Route =
    census.statsDirective {
      path("success") {
        get {
          AppCustomStatsRecorder.incTotalCalls("200")
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Say hello"))
        }
      } ~
        path("fail") {
          get {
            AppCustomStatsRecorder.incTotalCalls("500")
            complete(StatusCodes.InternalServerError)
          }
        }
    }

}
