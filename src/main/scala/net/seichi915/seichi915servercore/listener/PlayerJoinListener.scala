package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.menu.MultiBreakSettingMenu
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.boss.{BarColor, BarStyle}
import org.bukkit.enchantments.Enchantment
import org.bukkit.{Bukkit, ChatColor, Material, NamespacedKey}
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.{ItemFlag, ItemStack}

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
              Bukkit.getScheduler.runTask(
                Seichi915ServerCore.instance,
                (() =>
                   event.getPlayer
                     .kickPlayer("プレイヤーデータの作成に失敗しました。".toErrorMessage)): Runnable)
          }
        } else
          Database.updatePlayerNameIfChanged(event.getPlayer) onComplete {
            case Success(_) =>
              Seichi915ServerCore.playerDataMap += event.getPlayer -> value.get
              val bossBar = Seichi915ServerCore.instance.getServer
                .createBossBar(
                  new NamespacedKey(Seichi915ServerCore.instance,
                                    s"${event.getPlayer.getName}_BossBar"),
                  s"総整地量: ${Seichi915ServerCore.playerDataMap(event.getPlayer).getTotalBreakAmount}",
                  BarColor.WHITE,
                  BarStyle.SOLID
                )
              bossBar.setProgress(1.0)
              bossBar.addPlayer(event.getPlayer)
              Seichi915ServerCore.bossBarMap += event.getPlayer -> bossBar
              Bukkit.getScheduler.runTask(
                Seichi915ServerCore.instance,
                (() => {
                  val inventory = event.getPlayer.getInventory
                  inventory.clear()
                  val openMultiBreakSettingButton =
                    new ItemStack(Material.DIAMOND_PICKAXE)
                  val openMultiBreakSettingButtonMeta =
                    openMultiBreakSettingButton.getItemMeta
                  openMultiBreakSettingButtonMeta.setDisplayName(
                    s"${ChatColor.AQUA}マルチブレイク設定を開く")
                  openMultiBreakSettingButtonMeta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS)
                  openMultiBreakSettingButton.setItemMeta(
                    openMultiBreakSettingButtonMeta)
                  openMultiBreakSettingButton
                    .addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
                  openMultiBreakSettingButton.setClickAction { player =>
                    MultiBreakSettingMenu.open(player)
                    player.playMenuButtonClickSound()
                  }
                  inventory.setItem(9, openMultiBreakSettingButton)
                  val pickaxe = new ItemStack(Material.DIAMOND_PICKAXE)
                  val pickaxeMeta = pickaxe.getItemMeta
                  pickaxeMeta.setUnbreakable(true)
                  pickaxeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                                           ItemFlag.HIDE_UNBREAKABLE)
                  pickaxe.setItemMeta(pickaxeMeta)
                  pickaxe.addEnchantment(Enchantment.DIG_SPEED, 5)
                  pickaxe.addEnchantment(Enchantment.SILK_TOUCH, 1)
                  inventory.setItem(0, pickaxe)
                }): Runnable
              )
            case Failure(exception) =>
              exception.printStackTrace()
              Bukkit.getScheduler.runTask(
                Seichi915ServerCore.instance,
                (() =>
                   event.getPlayer
                     .kickPlayer("ユーザー名に更新に失敗しました。".toErrorMessage)): Runnable)
          }
      case Failure(exception) =>
        exception.printStackTrace()
        Bukkit.getScheduler.runTask(
          Seichi915ServerCore.instance,
          (() =>
             event.getPlayer
               .kickPlayer("プレイヤーデータの読み込みに失敗しました。".toErrorMessage)): Runnable)
    }
}
