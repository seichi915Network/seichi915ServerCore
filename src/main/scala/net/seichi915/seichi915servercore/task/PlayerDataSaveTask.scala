package net.seichi915.seichi915servercore.task

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.playerdata.PlayerData
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class PlayerDataSaveTask extends BukkitRunnable {
  override def run(): Unit =
    Seichi915ServerCore.playerDataMap.foreach {
      case (player: Player, playerData: PlayerData) =>
        playerData.save(player) onComplete {
          case Success(_) =>
          case Failure(exception) =>
            exception.printStackTrace()
            Seichi915ServerCore.instance.getLogger
              .warning(s"${player.getName}さんのプレイヤーデータのセーブに失敗しました。")
        }
    }
}
