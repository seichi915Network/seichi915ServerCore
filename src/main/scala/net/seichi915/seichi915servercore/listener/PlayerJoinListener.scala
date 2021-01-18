package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class PlayerJoinListener extends Listener {
  @EventHandler
  def onPlayerJoin(event: PlayerJoinEvent): Unit =
    event.getPlayer.getPlayerData onComplete {
      case Success(value) =>
        if (value.isEmpty) {
          Seichi915ServerCore.instance.getLogger
            .info(s"${event.getPlayer.getName}さんのプレイヤーデータが見つかりませんでした。作成します。")
          event.getPlayer.createNewPlayerData onComplete {
            case Success(_) =>
              onPlayerJoin(event)
            case Failure(exception) =>
              exception.printStackTrace()
              event.getPlayer.kickPlayer("プレイヤーデータの作成に失敗しました。".toErrorMessage)
          }
        } else
          Database.updatePlayerNameIfChanged(event.getPlayer) onComplete {
            case Success(_) =>
              Seichi915ServerCore.playerDataMap += event.getPlayer -> value.get
            case Failure(exception) =>
              exception.printStackTrace()
              event.getPlayer.kickPlayer("ユーザー名に更新に失敗しました。".toErrorMessage)
          }
      case Failure(exception) =>
        exception.printStackTrace()
        event.getPlayer.kickPlayer("プレイヤーデータの読み込みに失敗しました。".toErrorMessage)
    }
}
