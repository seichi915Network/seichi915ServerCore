package net.seichi915.seichi915servercore.task

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Scoreboard

import scala.jdk.CollectionConverters._

class ScoreboardResetTask extends BukkitRunnable {
  override def run(): Unit =
    Seichi915ServerCore.scoreboardMap.foreach {
      case (player: Player, scoreboard: Scoreboard) =>
        scoreboard.getObjectives.asScala.foreach(_.unregister())
        player.updateScoreboard()
    }
}
