package net.seichi915.seichi915servercore.playerdata

import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.multibreak.MultiBreak
import org.bukkit.entity.Player

import scala.concurrent.Future

case class PlayerData(totalBreakAmount: Long,
                      multiBreakEnabled: Boolean,
                      multiBreak: MultiBreak) {
  def getTotalBreakAmount: Long = totalBreakAmount

  def isMultiBreakEnabled: Boolean = multiBreakEnabled

  def getMultiBreak: MultiBreak = multiBreak

  def save(player: Player): Future[Unit] = Database.savePlayerData(player, this)
}
