package net.seichi915.seichi915servercore.playerdata

import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.multibreak.MultiBreak
import org.bukkit.entity.Player

import scala.concurrent.Future

case class PlayerData(var totalBreakAmount: Long,
                      var rank: Int,
                      var exp: Int,
                      var multiBreakEnabled: Boolean,
                      var multiBreak: MultiBreak,
                      var liquidHardenerEnabled: Boolean,
                      var liquidHardener: MultiBreak) {
  def getTotalBreakAmount: Long = totalBreakAmount

  def setTotalBreakAmount(totalBreakAmount: Long): Unit =
    this.totalBreakAmount = totalBreakAmount

  def getRank: Int = rank

  def setRank(rank: Int): Unit = this.rank = rank

  def getExp: Int = exp

  def setExp(exp: Int): Unit = this.exp = exp

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

  def getMaxMultiBreakSize: Int =
    if (getTotalBreakAmount >= 10000000000L) 17
    else if (getTotalBreakAmount >= 1000000000L) 15
    else if (getTotalBreakAmount >= 100000000L) 13
    else if (getTotalBreakAmount >= 10000000L) 11
    else if (getTotalBreakAmount >= 1000000L) 9
    else if (getTotalBreakAmount >= 100000L) 7
    else if (getTotalBreakAmount >= 10000L) 5
    else 3

  def save(player: Player): Future[Unit] = Database.savePlayerData(player, this)
}
