package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.menu.{
  BreakAmountRankingMenu,
  LiquidHardenerSettingMenu,
  MultiBreakSettingMenu
}
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.boss.{BarColor, BarStyle}
import org.bukkit.enchantments.Enchantment
import org.bukkit.{Bukkit, ChatColor, GameMode, Material, NamespacedKey}
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
                  event.getPlayer.setGameMode(GameMode.SURVIVAL)
                  val playerData =
                    Seichi915ServerCore.playerDataMap(event.getPlayer)
                  while (playerData.canRankUp) {
                    event.getPlayer.playRankUpSound()
                    event.getPlayer.sendMessage(
                      s"ランクアップしました。(${ChatColor.YELLOW}${playerData.getRank} ${ChatColor.RESET} -> ${ChatColor.GREEN}${playerData.getRank + 1}${ChatColor.RESET})".toSuccessMessage)
                    playerData.setRank(playerData.getRank + 1)
                    playerData.setExp(playerData.getExp - BigDecimal(2000.0))
                  }
                  event.getPlayer.setLevel(playerData.getRank)
                  event.getPlayer.setExp(
                    (playerData.getExp / BigDecimal(2000f)).floatValue)
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
                  inventory.setItem(10, openMultiBreakSettingButton)
                  val openLiquidHardenerSettingButton =
                    new ItemStack(Material.IRON_PICKAXE)
                  val openLiquidHardenerSettingButtonMeta =
                    openLiquidHardenerSettingButton.getItemMeta
                  openLiquidHardenerSettingButtonMeta.setDisplayName(
                    s"${ChatColor.AQUA}液体絶対固めるマン設定を開く")
                  openLiquidHardenerSettingButtonMeta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS)
                  openLiquidHardenerSettingButton.setItemMeta(
                    openLiquidHardenerSettingButtonMeta)
                  openLiquidHardenerSettingButton
                    .addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1)
                  openLiquidHardenerSettingButton.setClickAction { player =>
                    LiquidHardenerSettingMenu.open(player)
                    player.playMenuButtonClickSound()
                  }
                  inventory.setItem(11, openLiquidHardenerSettingButton)
                  val openBreakAmountRankingButton =
                    new ItemStack(Material.NETHER_STAR)
                  val openBreakAmountRankingButtonMeta =
                    openBreakAmountRankingButton.getItemMeta
                  openBreakAmountRankingButtonMeta.setDisplayName(
                    s"${ChatColor.AQUA}総整地量ランキングを開く")
                  openBreakAmountRankingButtonMeta.addItemFlags(
                    ItemFlag.HIDE_ATTRIBUTES)
                  openBreakAmountRankingButton.setItemMeta(
                    openBreakAmountRankingButtonMeta)
                  openBreakAmountRankingButton.setClickAction { player =>
                    BreakAmountRankingMenu.open(player)
                    player.playMenuButtonClickSound()
                  }
                  inventory.setItem(34, openBreakAmountRankingButton)
                  val pickaxe = new ItemStack(Material.DIAMOND_PICKAXE)
                  val pickaxeMeta = pickaxe.getItemMeta
                  pickaxeMeta.setUnbreakable(true)
                  pickaxeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                                           ItemFlag.HIDE_UNBREAKABLE)
                  pickaxe.setItemMeta(pickaxeMeta)
                  pickaxe.addEnchantment(Enchantment.DIG_SPEED, 5)
                  pickaxe.addEnchantment(Enchantment.SILK_TOUCH, 1)
                  inventory.setItem(0, pickaxe)
                  event.getPlayer.setPlayerInfoSkull()
                  val scoreboardManager = Bukkit.getScoreboardManager
                  val scoreboard = scoreboardManager.getNewScoreboard
                  event.getPlayer.setScoreboard(scoreboard)
                  Seichi915ServerCore.scoreboardMap += event.getPlayer -> scoreboard
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
