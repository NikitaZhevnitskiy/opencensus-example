package ru.zhenik.opencensus.example.akkacensus

import akka.http.scaladsl.model.{HttpRequest, Uri}

// https://github.com/rerorero/opencensus-prometheus-grafana-dashboard
case class RequestContext(
  started: Long,
  uri: Uri,
  lengthOption: Option[Long],
  hostname: String,
  method: String
)

object RequestContext {
  def fromRequest(req: HttpRequest, started: Long): RequestContext = {
    RequestContext(started, req.uri, req.entity.contentLengthOption, req.uri.authority.host.address(), req.method.value.capitalize)
  }
}