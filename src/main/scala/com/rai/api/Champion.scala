package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetAll(freeToPlay: Boolean = false)
case class GetById(id: Int)

class Champion extends RiotApi {
  def receive = {
    case GetAll(freeToPlay) => getAll(freeToPlay)
    case GetById(champ) => getById(champ)
    case res: Response => returnResults(res)
  }

  def getAll(freeToPlay: Boolean) = {
    val url = base_uri + "/api/lol/" + region + "/v1.2/champion"
    if (freeToPlay)
      params += ("freeToPlay" -> "true")
    RiotRetriever.getData(self, url, params)
  }

  def getById(id: Int) = {
    val url = base_uri + "/api/lol/" + region + "/v1.2/champion/" + id.toString
    RiotRetriever.getData(self, url, params)
  }
}
