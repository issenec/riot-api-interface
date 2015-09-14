package com.rai.api

object RankedMatchTypes extends Enumeration {
  type RankedMatchTypes = Value
  val RANKED_SOLO_5X5, RANKED_TEAM_3X3, RANKED_TEAM_5X5 = Value
}

object MatchTypes extends Enumeration {
  type MatchTypes = Value
  val RANKED_SOLO_5X5, RANKED_TEAM_3X3, RANKED_TEAM_5X5 = Value
}
