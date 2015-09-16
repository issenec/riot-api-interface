package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetStatus()
case class GetStatusFor(region: String)

class Status extends RiotApi {
  val statusUrl = "http://status.leagueoflegends.com/shards"

  def receive = {
    case GetStatus => getStatus
    case GetStatusFor(region) => getStatusFor(region)
    case res: Response => printResponse(res)
  }

  def getStatus = {
    val url = statusUrl
    RiotRetriever.getData(self, url, params)
  }

  def getStatusFor(region: String) = {
    val url = statusUrl + "/" + region
    RiotRetriever.getData(self, url, params)
  }
}
