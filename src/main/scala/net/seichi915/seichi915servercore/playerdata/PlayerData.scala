package net.seichi915.seichi915servercore.playerdata

import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.multibreak.MultiBreak
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

import scala.concurrent.Future

case class PlayerData(var totalBreakAmount: Long,
                      var rank: Int,
                      var exp: BigDecimal,
                      var expBoost: BigDecimal,
                      var votePoint: Int,
                      var multiBreakEnabled: Boolean,
                      var multiBreak: MultiBreak,
                      var liquidHardenerEnabled: Boolean,
                      var liquidHardener: MultiBreak,
                      var speedEffectEnabled: Boolean,
                      var speedEffectAmplifier: Int,
                      var hasteEffectEnabled: Boolean,
                      var hasteEffectAmplifier: Int,
                      var jumpBoostEffectEnabled: Boolean,
                      var jumpBoostEffectAmplifier: Int,
                      var nightVisionEffectEnabled: Boolean) {
  def getTotalBreakAmount: Long = totalBreakAmount

  def setTotalBreakAmount(totalBreakAmount: Long): Unit =
    this.totalBreakAmount = totalBreakAmount

  def getRank: Int = rank

  def setRank(rank: Int): Unit = this.rank = rank

  def getExp: BigDecimal = exp

  def setExp(exp: BigDecimal): Unit =
    this.exp = if (getRank < 1000) exp else BigDecimal(0.0)

  def getExpBoost: BigDecimal =
    if (getRank < 1000) expBoost else BigDecimal(0.0)

  def setExpBoost(expBoost: BigDecimal): Unit =
    this.expBoost = if (getRank < 1000) expBoost else BigDecimal(0.0)

  def getVotePoint: Int = votePoint

  def setVotePoint(votePoint: Int): Unit = this.votePoint = votePoint

  def isMultiBreakEnabled: Boolean = multiBreakEnabled

  def setMultiBreakEnabled(multiBreakEnabled: Boolean): Unit =
    this.multiBreakEnabled = multiBreakEnabled

  def getMultiBreak: MultiBreak = multiBreak

  def setMultiBreak(multiBreak: MultiBreak): Unit = this.multiBreak = multiBreak

  def isLiquidHardenerEnabled: Boolean = liquidHardenerEnabled

  def setLiquidHardenerEnabled(liquidHardenerEnabled: Boolean): Unit =
    this.liquidHardenerEnabled = liquidHardenerEnabled

  def getLiquidHardener: MultiBreak = liquidHardener

  def setLiquidHardener(liquidHardener: MultiBreak): Unit =
    this.liquidHardener = liquidHardener

  def isSpeedEffectEnabled: Boolean = speedEffectEnabled

  def setSpeedEffectEnabled(speedEffectEnabled: Boolean): Unit =
    this.speedEffectEnabled = speedEffectEnabled

  def getSpeedEffectAmplifier: Int = speedEffectAmplifier

  def setSpeedEffectAmplifier(speedEffectAmplifier: Int): Unit =
    this.speedEffectAmplifier = speedEffectAmplifier

  def isHasteEffectEnabled: Boolean = hasteEffectEnabled

  def setHasteEffectEnabled(hasteEffectEnabled: Boolean): Unit =
    this.hasteEffectEnabled = hasteEffectEnabled

  def getHasteEffectAmplifier: Int = hasteEffectAmplifier

  def setHasteEffectAmplifier(hasteEffectAmplifier: Int): Unit =
    this.hasteEffectAmplifier = hasteEffectAmplifier

  def isJumpBoostEffectEnabled: Boolean = jumpBoostEffectEnabled

  def setJumpBoostEffectEnabled(jumpBoostEffectEnabled: Boolean): Unit =
    this.jumpBoostEffectEnabled = jumpBoostEffectEnabled

  def getJumpBoostEffectAmplifier: Int = jumpBoostEffectAmplifier

  def setJumpBoostEffectAmplifier(jumpBoostEffectAmplifier: Int): Unit =
    this.jumpBoostEffectAmplifier = jumpBoostEffectAmplifier

  def isNightVisionEffectEnabled: Boolean = nightVisionEffectEnabled

  def setNightVisionEffectEnabled(nightVisionEffectEnabled: Boolean): Unit =
    this.nightVisionEffectEnabled = nightVisionEffectEnabled

  def getRanking(player: Player): Int = {
    Database.getPlayerAndBreakAmount.sortBy(_._2).reverse.zipWithIndex.foreach {
      case ((offlinePlayer: OfflinePlayer, _), index: Int) =>
        if (offlinePlayer.getUniqueId == player.getUniqueId) return index + 1
    }
    0
  }

  def getMaxMultiBreakSize: Int =
    if (getTotalBreakAmount >= 10000000000L) 17
    else if (getTotalBreakAmount >= 1000000000L) 15
    else if (getTotalBreakAmount >= 100000000L) 13
    else if (getTotalBreakAmount >= 10000000L) 11
    else if (getTotalBreakAmount >= 1000000L) 9
    else if (getTotalBreakAmount >= 100000L) 7
    else if (getTotalBreakAmount >= 10000L) 5
    else 3

  def canRankUp: Boolean = getRank < 1000 && getExp >= BigDecimal(2000.0)

  def save(player: Player): Future[Unit] = Database.savePlayerData(player, this)
}
