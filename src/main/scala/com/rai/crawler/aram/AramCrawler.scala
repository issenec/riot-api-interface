package com.rai.crawler.aram

import akka.actor.{ActorSystem, Props}
import com.rai.api._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import uk.co.robinmurphy.http.Response

case class StartMessage()

class AramCrawler extends RiotApi {
  val system = ActorSystem("AramCrawler")
  val gameActor = system.actorOf(Props(new Game))
  val matchActor = system.actorOf(Props(new Match))

  def receive = {
    case StartMessage => seedMatches()
    case res: Response => parseJson(res)
  }

  def parseJson(res:Response) = {
    val json = parse(res.body)
    val matchJson = (json \ "mapId").toOption
    println("JSON: " + res.body)
    matchJson match {
      case Some(s) => {
        println("Saving match " + (json \ "matchId").toString)
        sendToEs(res, compact(render(json \ "matchId")), esConf.getString("es.type.aram"))
      }
      case None => {
        println("Received recent games for " + (json \ "summonerId").toString)
        parseGameJson(json)
      }
    }
  }

  def parseGameJson(json: JValue) = {
    val gameIds = for {
      game @ JObject(x) <- json \ "games"
      if x contains JField("gameMode", JString("ARAM"))
      JInt(gameId) <- game \ "gameId" } yield gameId.longValue()
    val summonerIds = (for {
      game @ JObject(x) <- json \ "games"
      if x contains JField("gameMode", JString("ARAM"))
      JInt(summonerId) <- game \"fellowPlayers" \\ "summonerId" } yield summonerId.longValue()).toSet
    for (gameId <- gameIds) matchActor ! GetMatch(gameId, includeTimeline = true)
    for (summonerId <- summonerIds) gameActor ! GetRecentGames(summonerId)
  }

  def seedMatches() = {
    gameActor ! GetMyRecentGames()
  }
}

object AramCrawler {
  def main(args: Array[String]) {
    val system = ActorSystem("AramCrawler")
    val crawler = system.actorOf(Props(new AramCrawler))
    crawler ! StartMessage
  }
}
