package com.rai.api

import com.rai.crawler.RiotRetriever
import uk.co.robinmurphy.http.Response

case class GetFeaturedGames

class FeaturedGames extends RiotApi {
  def receive = {
    case GetFeaturedGames => getFeaturedGames
    case res: Response => returnResults(res)
  }

  def getFeaturedGames = {
    val url = base_uri + "/observer-mode/rest/featured"
    RiotRetriever.getData(self, url, params)
  }
}
