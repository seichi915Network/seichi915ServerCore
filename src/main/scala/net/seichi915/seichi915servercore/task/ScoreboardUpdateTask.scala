package net.seichi915.seichi915servercore.task

import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

import scala.jdk.CollectionConverters._

class ScoreboardUpdateTask extends BukkitRunnable {
  override def run(): Unit =
    Bukkit.getOnlinePlayers.asScala.foreach(_.updateScoreboard())
}
