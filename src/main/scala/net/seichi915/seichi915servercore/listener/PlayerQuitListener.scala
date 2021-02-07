package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class PlayerQuitListener extends Listener {
  @EventHandler
  def onPlayerQuit(event: PlayerQuitEvent): Unit = {
    Seichi915ServerCore.bossBarMap.get(event.getPlayer) match {
      case Some(bossBar) =>
        bossBar.removeAll()
        Seichi915ServerCore.instance.getServer.removeBossBar(
          new NamespacedKey(Seichi915ServerCore.instance,
                            s"${event.getPlayer.getName}_BossBar"))
        Seichi915ServerCore.bossBarMap.remove(event.getPlayer)
      case _ =>
    }
    Seichi915ServerCore.playerDataMap
      .getOrElse(event.getPlayer, {
        Seichi915ServerCore.instance.getLogger.warning(
          s"${event.getPlayer.getName}さんのプレイヤーデータが見つかりませんでした。")
        return
      })
      .save(event.getPlayer) onComplete {
      case Success(_) =>
        Seichi915ServerCore.playerDataMap.remove(event.getPlayer)
      case Failure(exception) =>
        exception.printStackTrace()
        Seichi915ServerCore.instance.getLogger
          .warning(s"${event.getPlayer.getName}さんのプレイヤーデータのセーブに失敗しました。")
        Seichi915ServerCore.playerDataMap.remove(event.getPlayer)
    }
  }
}
