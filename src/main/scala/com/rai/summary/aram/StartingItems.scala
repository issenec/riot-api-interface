package com.rai.summary.aram

import akka.actor.{ActorLogging, Actor, Props, ActorSystem}
import com.sksamuel.elastic4s.{ElasticsearchClientUri, ElasticClient}
import com.sksamuel.elastic4s.ElasticDsl._
import com.typesafe.config.ConfigFactory
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.common.settings.ImmutableSettings
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}


case class ItemPurchaseEvent(timestamp: Long, itemId: Long, participantId: Long)
case class ChampionKillEvent(timestamp: Long, killerId: Long, victimId: Long)

case class StartingItemsCounts(items: String, count: Int, winner: Int)
case class ChampionStartingItems(id: Long, startingItems: StartingItemsCounts)

class StartingItems extends Actor with ActorLogging {
  val conf = ConfigFactory.load("riot.conf")
  val esConf = ConfigFactory.load("elasticsearch.conf")

  val key = conf.getString("dev.key")
  val region = conf.getString("dev.region")
  val mySummonerId = conf.getString("dev.summoner.id")

  val uri = ElasticsearchClientUri(conf.getString("es.address"))
  val settings = ImmutableSettings.settingsBuilder().put("cluster.name", conf.getString("es.cluster")).build()
  val esClient = ElasticClient.remote(settings, uri)

  def receive = {
    case StartMessage => seedStartingItems()
    case items: ChampionStartingItems => updateCounts(items)
  }

  def earliestKills(k1: ChampionKillEvent, k2: ChampionKillEvent): ChampionKillEvent =
    if(k1.timestamp < k2.timestamp) k1 else k2

  def getStartingItems(resp: SearchResponse) = {
    val matches = resp.getHits.getHits.map { f =>
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
      val championTuples = participants.map { p =>
        if (p._3 == winningTeam)
          (p._1, p._2, 1)
        else
          (p._1, p._2, 0)
      }
      val kills = for {
        events@JObject(x) <- json \ "timeline" \ "frames" \ "events"
        if x contains JField("eventType", JString("CHAMPION_KILL"))
        JObject(event) <- events
        JField("timestamp", JInt(timestamp)) <- event
        JField("killerId", JInt(killerId)) <- event
        JField("victimId", JInt(victimId)) <- event
      } yield ChampionKillEvent(timestamp.longValue, killerId.longValue, victimId.longValue)
      val killTuples = (1 to 10).map { participantId =>
        val participantDeaths = kills.filter(kill => kill.victimId.longValue == participantId.toLong)
        if (participantDeaths.isEmpty)
        /** Return tuple of extremely long time, irrelevant killer (since not used), and deathless victimId */
          ChampionKillEvent(999999999.toLong, 11.toLong, participantId.toLong)
        else
          participantDeaths.reduceLeft(earliestKills)
      }
      val items = for {
        events@JObject(x) <- json \ "timeline" \ "frames" \ "events"
        if x contains JField("eventType", JString("ITEM_PURCHASED"))
        JObject(event) <- events
        JField("timestamp", JInt(timestamp)) <- event
        JField("itemId", JInt(itemId)) <- event
        JField("participantId", JInt(participantId)) <- event
      } yield ItemPurchaseEvent(timestamp.longValue(), itemId.longValue(), participantId.longValue())
      val itemTuples = (1 to 10).map { participantId =>
        val killedTime = killTuples(participantId - 1).timestamp
        items.filter(item => item.participantId == participantId.toLong && item.timestamp < killedTime)
          .map(item => item.itemId).filter(id => id != 2003.toLong && id != 2004.toLong && id != 2010.toLong).toSet
      }
      (championTuples, itemTuples).zipped.map { (champion, startingItems) =>
        val startingItemCounts = StartingItemsCounts(startingItems.toSeq.sorted.mkString("_"), 1, champion._3)
        ChampionStartingItems(champion._2, startingItemCounts)
      }
    }
    matches.foreach { game =>
      game.foreach { items =>
        updateCounts(items)
      }
    }
  }

  def seedStartingItems() = {
    /** Get some ARAM games */
    val resp = esClient.execute {
      search in "league-of-legends" / "arams" query { matchQuery("matchType", "MATCHED_GAME") } scroll "1m" limit 100
    }.await

    /** Get a list of tuples to link the participant to their champion: (participantId, championId, winner) */
    getStartingItems(resp)
  }

  def updateCounts(items: ChampionStartingItems) = {
    val counterScript = "ctx._source.starting_items.si" + items.startingItems.items + ".counter += count"
    val winnerScript = "ctx._source.starting_items.si" + items.startingItems.items + ".winner += count"
    val resp = esClient.execute {
      update(items.id).in("league-of-legends/champions") script(counterScript) params(Map("count" -> 1))
      update(items.id).in("league-of-legends/champions") script(winnerScript) params(Map("count" -> items.startingItems.winner))
    }
    resp onComplete {
      case Success(res) => println("Updated counts for " + items.id.toString)
      case Failure(t) =>
        println("Error: " + t.getCause.toString)
        esClient.execute {
          index into "league-of-legends/champions" id items.id fields (
            "starting_items" -> Map(("si" + items.startingItems.items) ->
              Map("count" -> items.startingItems.count, "winner" -> items.startingItems.winner)
            )
          )
        }.await
    }
  }
}

object StartingItems {
  def main(args: Array[String]) {
    val system = ActorSystem("StartingItems")
    val itemsActor = system.actorOf(Props(new StartingItems))
    itemsActor ! StartMessage
  }
}