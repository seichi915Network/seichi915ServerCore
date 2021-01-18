package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class PlayerQuitListener extends Listener {
  @EventHandler
  def onPlayerQuit(event: PlayerQuitEvent): Unit =
    Seichi915ServerCore.playerDataMap
      .getOrElse(event.getPlayer, {
        Seichi915ServerCore.instance.getLogger.warning(
          s"${event.getPlayer}さんのプレイヤーデータが見つかりませんでした。")
        return
      })
      .save(event.getPlayer) onComplete {
      case Success(_) =>
        Seichi915ServerCore.playerDataMap.remove(event.getPlayer)
      case Failure(exception) =>
        exception.printStackTrace()
        Seichi915ServerCore.instance.getLogger
          .warning(s"${event.getPlayer}さんのプレイヤーデータのセーブに失敗しました。")
        Seichi915ServerCore.playerDataMap.remove(event.getPlayer)
    }
}
