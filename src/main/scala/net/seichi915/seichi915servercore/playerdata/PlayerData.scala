package net.seichi915.seichi915servercore.playerdata

import net.seichi915.seichi915servercore.database.Database
import org.bukkit.entity.Player

import scala.concurrent.Future

case class PlayerData(totalBreakAmount: Long) {
  def getTotalBreakAmount: Long = totalBreakAmount

  def save(player: Player): Future[Unit] = Database.savePlayerData(player, this)
}
