package net.seichi915.seichi915servercore

import net.seichi915.seichi915servercore.command._
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.listener._
import net.seichi915.seichi915servercore.meta.menu.ClickAction
import net.seichi915.seichi915servercore.playerdata.PlayerData
import net.seichi915.seichi915servercore.task._
import org.bukkit.Bukkit
import org.bukkit.boss.BossBar
import org.bukkit.command.{CommandExecutor, TabCompleter}
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Scoreboard

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Seichi915ServerCore {
  var instance: Seichi915ServerCore = _

  var playerDataMap: mutable.HashMap[Player, PlayerData] = mutable.HashMap()
  var clickActionMap: mutable.HashMap[UUID, ClickAction] = mutable.HashMap()
  var bossBarMap: mutable.HashMap[Player, BossBar] = mutable.HashMap()
  var scoreboardMap: mutable.HashMap[Player, Scoreboard] = mutable.HashMap()
  var previousBreakAmountMap: mutable.HashMap[Player, Long] = mutable.HashMap()
}

class Seichi915ServerCore extends JavaPlugin {
  Seichi915ServerCore.instance = this

  override def onEnable(): Unit = {
    if (!Database.saveDefaultDatabase) {
      getLogger.severe("デフォルトのデータベースファイルのコピーに失敗しました。サーバーを停止します。")
      Bukkit.shutdown()
      return
    }
    Seq(
      new BlockBreakListener,
      new InventoryClickListener,
      new PlayerDropItemListener,
      new PlayerInteractListener,
      new PlayerJoinListener,
      new PlayerMoveListener,
      new PlayerQuitListener,
      new PlayerSeichiListener,
      new PlayerSwapHandItemsListener
    ).foreach(Bukkit.getPluginManager.registerEvents(_, this))
    Map(
      "fly" -> new FlyCommand
    ).foreach {
      case (commandName: String, commandExecutor: CommandExecutor) =>
        Bukkit.getPluginCommand(commandName).setExecutor(commandExecutor)
        Bukkit
          .getPluginCommand(commandName)
          .setTabCompleter(commandExecutor.asInstanceOf[TabCompleter])
    }
    Map(
      (6000, 6000) -> new PlayerDataSaveTask,
      (15, 15) -> new PotionEffectUpdateTask,
      (72000, 72000) -> new ScoreboardResetTask,
      (20, 20) -> new ScoreboardUpdateTask
    ).foreach {
      case ((delay: Int, period: Int), bukkitRunnable: BukkitRunnable) =>
        bukkitRunnable.runTaskTimer(this, delay, period)
    }

    getLogger.info("seichi915ServerCoreが有効になりました。")
  }

  override def onDisable(): Unit = {
    Seichi915ServerCore.playerDataMap.foreach {
      case (player: Player, playerData: PlayerData) =>
        playerData.save(player) onComplete {
          case Success(_) =>
            Seichi915ServerCore.playerDataMap.remove(player)
          case Failure(exception) =>
            exception.printStackTrace()
            Seichi915ServerCore.instance.getLogger
              .warning(s"${player.getName}さんのプレイヤーデータのセーブに失敗しました。")
            Seichi915ServerCore.playerDataMap.remove(player)
        }
    }

    getLogger.info("seichi915ServerCoreが無効になりました。")
  }
}
