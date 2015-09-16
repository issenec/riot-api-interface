package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetRankedFor(summonerId: Long, season: String = "SEASON2015")
case class GetSummaryFor(summonerId: Long, season: String = "SEASON2015")

class Stats extends RiotApi {
  val statsUrl = baseUri + "/api/lol/" + region + "/v1.3/stats/by-summoner/"

  def receive = {
    case GetRankedFor(summonerId, season) => getRankedFor(summonerId, season)
    case GetSummaryFor(summonerId, season) => getSummaryFor(summonerId, season)
    case res: Response => printResponse(res)
  }

  def getRankedFor(summonerId: Long, season: String) = {
    val url = statsUrl + summonerId.toString + "/ranked"
    params += ("season" -> season)
    RiotRetriever.getData(self, url, params)
  }

  def getSummaryFor(summonerId: Long, season: String) = {
    val url = statsUrl + summonerId.toString + "/summary"
    params += ("season" -> season)
    RiotRetriever.getData(self, url, params)
  }
}
