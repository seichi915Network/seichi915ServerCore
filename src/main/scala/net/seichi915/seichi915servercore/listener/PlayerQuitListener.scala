package net.seichi915.seichi915servercore.listener

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

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
      case None =>
    }
    Seichi915ServerCore.scoreboardMap.get(event.getPlayer) match {
      case Some(scoreboard) =>
        scoreboard.getObjectives.asScala.foreach(_.unregister())
      case None =>
    }
    Seichi915ServerCore.previousBreakAmountMap.get(event.getPlayer) match {
      case Some(_) =>
        Seichi915ServerCore.previousBreakAmountMap.remove(event.getPlayer)
      case None =>
    }
    val playerData = Seichi915ServerCore.playerDataMap
      .getOrElse(event.getPlayer, {
        Seichi915ServerCore.instance.getLogger
          .warning(s"${event.getPlayer.getName}さんのプレイヤーデータが見つかりませんでした。")
        return
      })
    val task = IO {
      try playerData.save(event.getPlayer)
      catch {
        case e: Exception =>
          e.printStackTrace()
          Seichi915ServerCore.instance.getLogger
            .warning(s"${event.getPlayer.getName}さんのプレイヤーデータのセーブに失敗しました。")
      } finally Seichi915ServerCore.playerDataMap.remove(event.getPlayer)
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
  }
}
