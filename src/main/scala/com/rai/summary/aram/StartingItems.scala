package com.rai.summary.aram

import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import com.typesafe.config.ConfigFactory
import org.elasticsearch.common.settings.ImmutableSettings
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

case class StartingItems(items: Set[Long], count: Long, winner: Long)
case class ItemPurchaseEvent(timestamp: Long, itemId: Long, participantId: Long)
case class ChampionKillEvent(timestamp: Long, killerId: Long, victimId: Long)

object StartingItmes {
  val conf = ConfigFactory.load("riot.conf")
  val esConf = ConfigFactory.load("elasticsearch.conf")

  val key = conf.getString("dev.key")
  val region = conf.getString("dev.region")
  val mySummonerId = conf.getString("dev.summoner.id")

  val uri = ElasticsearchClientUri(conf.getString("es.address"))
  val settings = ImmutableSettings.settingsBuilder().put("cluster.name", conf.getString("es.cluster")).build()
  val esClient = ElasticClient.remote(settings, uri)

  def earliestKills(k1: ChampionKillEvent, k2: ChampionKillEvent): ChampionKillEvent =
    if(k1.timestamp < k2.timestamp) k1 else k2

  def main(args: Array[String]) {
    /** Initialize the champion:startingItems map that should be stored */
    val startingItems = Map[Int, List[StartingItems]]()

    val resp = esClient.execute { search in "league-of-legends" / "arams" query {
      matchQuery("matchType", "MATCHED_GAME")
    } }.await

    val championMap = resp.getHits.getHits.map { f =>
      val json = parse(f.sourceAsString)
      val winningTeam = (for {
        teams@JObject(x) <- json \ "teams"
        JObject(team) <- teams
        JField("teamId", JInt(teamId)) <- team
        JField("winner", JBool(winner)) <- team
        if winner
      } yield teamId.longValue()).head
      val participants = for {
        participants@JObject(x) <- json \ "participants"
        JObject(participant) <- participants
        JField("participantId", JInt(participantId)) <- participant
        JField("championId", JInt(championId)) <- participant
        JField("teamId", JInt(teamId)) <- participant
      } yield (participantId.longValue(), championId.longValue(), teamId.longValue())
      participants.map { p =>
        if (p._3 == winningTeam)
          (p._1, p._2, 1)
        else
          (p._1, p._2, 0)
      }
    }

    val itemPurchaseEvents = resp.getHits.getHits.map { f =>
      val json = parse(f.sourceAsString)
      for {
        events@JObject(x) <- json \ "timeline" \ "frames" \ "events"
        if x contains JField("eventType", JString("ITEM_PURCHASED"))
        JObject(event) <- events
        JField("timestamp", JInt(timestamp)) <- event
        JField("itemId", JInt(itemId)) <- event
        JField("participantId", JInt(participantId)) <- event
      } yield ItemPurchaseEvent(timestamp.longValue(), itemId.longValue(), participantId.longValue())
    }

    val championKillEvents = resp.getHits.getHits.map { f =>
      val json = parse(f.sourceAsString)
      val kills = for {
        events@JObject(x) <- json \ "timeline" \ "frames" \ "events"
        if x contains JField("eventType", JString("CHAMPION_KILL"))
        JObject(event) <- events
        JField("timestamp", JInt(timestamp)) <- event
        JField("killerId", JInt(killerId)) <- event
        JField("victimId", JInt(victimId)) <- event
      } yield ChampionKillEvent(timestamp.longValue, killerId.longValue, victimId.longValue)
      (1 to 10).map { participantId =>
        kills.filter(kill => kill.victimId.longValue == participantId.toLong).reduceLeft(earliestKills)
      }
    }

    /**
     * @todo foreach champion, generate unique Set of StartingItems, unique on startingItems field
     */
    (championMap, itemPurchaseEvents, championKillEvents).zipped.map { (champions, items, kills) =>
      println("")
//      /** this gets all ItemPurchaseEvents for participant 1 in the game */
////      game.filter { x: ItemPurchaseEvent => x.participantId == 1 }
    }
    println(resp)
  }
}