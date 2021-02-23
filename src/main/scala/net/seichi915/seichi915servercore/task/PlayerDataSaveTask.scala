package net.seichi915.seichi915servercore.task

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.playerdata.PlayerData
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

import scala.concurrent.ExecutionContext

class PlayerDataSaveTask extends BukkitRunnable {
  override def run(): Unit = {
    val task = IO {
      Seichi915ServerCore.playerDataMap.foreach {
        case (player: Player, playerData: PlayerData) =>
          try playerData.save(player)
          catch {
            case e: Exception =>
              e.printStackTrace()
              Seichi915ServerCore.instance.getLogger
                .warning(s"${player.getName}さんのプレイヤーデータのセーブに失敗しました。")
          }
      }
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
  }
}
