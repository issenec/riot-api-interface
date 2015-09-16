package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetAll(freeToPlay: Boolean = false)
case class GetById(id: Int)

class Champion extends RiotApi {
  val championUrl = baseUri + "/api/lol/" + region + "/v1.2/champion"

  def receive = {
    case GetAll(freeToPlay) => getAll(freeToPlay)
    case GetById(champ) => getById(champ)
    case res: Response => printResponse(res)
  }

  def getAll(freeToPlay: Boolean) = {
    val url = championUrl
    if (freeToPlay)
      params += ("freeToPlay" -> "true")
    RiotRetriever.getData(sender, url, params)
  }

  def getById(id: Int) = {
    val url = championUrl + "/" + id.toString
    RiotRetriever.getData(sender, url, params)
  }
}
