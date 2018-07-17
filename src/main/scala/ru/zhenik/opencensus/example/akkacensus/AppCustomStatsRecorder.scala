package ru.zhenik.opencensus.example.akkacensus

import java.util.Collections

import io.opencensus.contrib.http.util.HttpViews
import io.opencensus.stats.Measure.MeasureLong
import io.opencensus.stats._
import io.opencensus.tags.{TagKey, TagValue, Tagger, Tags}

object AppCustomStatsRecorder {
  val tagger: Tagger =  Tags.getTagger
  val viewManager: ViewManager = Stats.getViewManager
  val statsRecorder: StatsRecorder = Stats.getStatsRecorder

  // const tag(s)...
  val TAG_KEY_STATUS: TagKey = TagKey.create("http_status_code")
  val APPLICATION_HTTP_MEASURE_LONG: MeasureLong = MeasureLong.create("application_http_request_total", "amount of calls", "By")
  viewManager.registerView(View.create(
    View.Name.create(APPLICATION_HTTP_MEASURE_LONG.getName),
    "amount of calls",
    APPLICATION_HTTP_MEASURE_LONG,
    Aggregation.Count.create(),
    Collections.singletonList(TAG_KEY_STATUS)))


  def register(): Unit = HttpViews.registerAllClientViews()

  def incTotalCalls(tagValue: String): Unit = statsRecorder
    .newMeasureMap.put(APPLICATION_HTTP_MEASURE_LONG, 1)
    .record(tagger.emptyBuilder().put(TAG_KEY_STATUS, TagValue.create(tagValue)).build())

}
