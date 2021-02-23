package net.seichi915.seichi915servercore.listener

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.builder.ItemStackBuilder
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.external.ExternalPlugins
import net.seichi915.seichi915servercore.menu._
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.boss.{BarColor, BarStyle}
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.{Bukkit, ChatColor, Color, GameMode, Material, NamespacedKey}
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.ItemFlag

import scala.concurrent.ExecutionContext

class PlayerJoinListener extends Listener {
  @EventHandler
  def onPlayerJoin(event: PlayerJoinEvent): Unit = {
    val task = IO {
      try {
        Database.getPlayerData(event.getPlayer) match {
          case Some(playerData) =>
            Database.updatePlayerNameIfChanged(event.getPlayer)
            Seichi915ServerCore.playerDataMap += event.getPlayer -> playerData
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
            event.getPlayer.setGameMode(GameMode.SURVIVAL)
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
            if (playerData.calcMaxMultiBreakSize > playerData.getMaxMultiBreakSize) {
              playerData.setMaxMultiBreakSize(playerData.calcMaxMultiBreakSize)
              event.getPlayer.sendMessage(
                s"マルチブレイクのサイズ上限が ${ChatColor.GREEN}${playerData.calcMaxMultiBreakSize} ${ChatColor.RESET}になりました。".toSuccessMessage)
              event.getPlayer.playChangeMaxMultiBreakSizeSound()
            }
            val inventory = event.getPlayer.getInventory
            inventory.clear()
            val openMultiBreakSettingButton =
              ItemStackBuilder(Material.DIAMOND_PICKAXE)
                .setDisplayName(s"${ChatColor.AQUA}マルチブレイク設定を開く")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .build
            openMultiBreakSettingButton.setClickAction { player =>
              MultiBreakSettingMenu.open(player)
              player.playMenuButtonClickSound()
            }
            inventory.setItem(10, openMultiBreakSettingButton)
            val openLiquidHardenerSettingButton =
              ItemStackBuilder(Material.IRON_PICKAXE)
                .setDisplayName(s"${ChatColor.AQUA}液体絶対固めるマン設定を開く")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                .build
            openLiquidHardenerSettingButton.setClickAction { player =>
              LiquidHardenerSettingMenu.open(player)
              player.playMenuButtonClickSound()
            }
            inventory.setItem(11, openLiquidHardenerSettingButton)
            val openPotionEffectSettingButton =
              ItemStackBuilder(Material.POTION)
                .setDisplayName(s"${ChatColor.AQUA}ポーションエフェクト設定を開く")
                .addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                              ItemFlag.HIDE_POTION_EFFECTS)
                .build
            val openPotionEffectSettingButtonMeta =
              openPotionEffectSettingButton.getItemMeta
                .asInstanceOf[PotionMeta]
            openPotionEffectSettingButtonMeta.setColor(Color.AQUA)
            openPotionEffectSettingButton.setItemMeta(
              openPotionEffectSettingButtonMeta)
            openPotionEffectSettingButton.setClickAction { player =>
              PotionEffectSettingMenu.open(player)
              player.playMenuButtonClickSound()
            }
            inventory.setItem(12, openPotionEffectSettingButton)
            val openBreakAmountRankingButton =
              ItemStackBuilder(Material.NETHER_STAR)
                .setDisplayName(s"${ChatColor.AQUA}総整地量ランキングを開く")
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build
            openBreakAmountRankingButton.setClickAction { player =>
              BreakAmountRankingMenu.open(player)
              player.playMenuButtonClickSound()
            }
            inventory.setItem(34, openBreakAmountRankingButton)
            def setToggleFlyingButton(player: Player): Unit = {
              val toggleFlyingButton = ItemStackBuilder(Material.ELYTRA)
                .setDisplayName(s"${ChatColor.AQUA}Fly切り替え")
                .addLore(
                  List(
                    s"現在Flyは ${if (player.isFlying) s"${ChatColor.GREEN}オン"
                    else s"${ChatColor.RED}オフ"} ${ChatColor.WHITE}になっています。",
                    "クリックでオン・オフを切り替えられます。"
                  ).map(str => s"${ChatColor.WHITE}$str")
                )
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build
              toggleFlyingButton.setClickAction { _ =>
                if (player.isFlying) {
                  player.setFlying(false)
                  player.setAllowFlight(false)
                } else {
                  player.setAllowFlight(true)
                  player.setFlying(true)
                }
                player.playMenuButtonClickSound()
                setToggleFlyingButton(player)
              }
              player.getInventory.setItem(33, toggleFlyingButton)
            }
            setToggleFlyingButton(event.getPlayer)
            val teleportToSeichiWorldButton =
              ItemStackBuilder(Material.GRASS_BLOCK)
                .setDisplayName(s"${ChatColor.AQUA}整地ワールドに移動")
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build
            teleportToSeichiWorldButton.setClickAction { player =>
              val seichiWorld =
                ExternalPlugins.getMultiverseCore.getMVWorldManager
                  .getMVWorld("SeichiWorld")
              player.teleport(seichiWorld.getSpawnLocation)
            }
            inventory.setItem(27, teleportToSeichiWorldButton)
            val teleportToNetherSeichiWorldButton =
              ItemStackBuilder(Material.NETHERRACK)
                .setDisplayName(s"${ChatColor.AQUA}ネザー整地ワールドに移動")
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build
            teleportToNetherSeichiWorldButton.setClickAction { player =>
              val seichiWorld =
                ExternalPlugins.getMultiverseCore.getMVWorldManager
                  .getMVWorld("NetherSeichiWorld")
              player.teleport(seichiWorld.getSpawnLocation)
            }
            inventory.setItem(28, teleportToNetherSeichiWorldButton)
            val teleportToEndSeichiWorldButton =
              ItemStackBuilder(Material.END_STONE)
                .setDisplayName(s"${ChatColor.AQUA}エンド整地ワールドに移動")
                .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                .build
            teleportToEndSeichiWorldButton.setClickAction { player =>
              val seichiWorld =
                ExternalPlugins.getMultiverseCore.getMVWorldManager
                  .getMVWorld("EndSeichiWorld")
              player.teleport(seichiWorld.getSpawnLocation)
            }
            inventory.setItem(29, teleportToEndSeichiWorldButton)
            val openShopButton = ItemStackBuilder(Material.EMERALD)
              .setDisplayName(s"${ChatColor.AQUA}ショップを開く")
              .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
              .build
            openShopButton.setClickAction { player =>
              ShopMenu.open(player)
              player.playMenuButtonClickSound()
            }
            inventory.setItem(17, openShopButton)
            val closeButton = ItemStackBuilder(Material.BARRIER)
              .setDisplayName(s"${ChatColor.RED}閉じる")
              .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
              .build
            closeButton.setClickAction { player =>
              player.closeInventory()
              player.playMenuButtonClickSound()
            }
            inventory.setItem(35, closeButton)
            val pickaxe = ItemStackBuilder(Material.DIAMOND_PICKAXE)
              .setUnbreakable(true)
              .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
              .addEnchantment(Enchantment.DIG_SPEED, 5)
              .addEnchantment(Enchantment.SILK_TOUCH, 1)
              .build
            inventory.setItem(0, pickaxe)
            event.getPlayer.setPlayerInfoSkull()
            val scoreboardManager = Bukkit.getScoreboardManager
            IO {
              val scoreboard = scoreboardManager.getNewScoreboard
              event.getPlayer.setScoreboard(scoreboard)
              Seichi915ServerCore.scoreboardMap += event.getPlayer -> scoreboard
            }.runOnServerThread(Seichi915ServerCore.instance)
          case None =>
            Seichi915ServerCore.instance.getLogger
              .info(s"${event.getPlayer.getName}さんのプレイヤーデータが見つかりませんでした。作成します。")
            Database.createNewPlayerData(event.getPlayer)
            onPlayerJoin(event)
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          IO(
            event.getPlayer
              .kickPlayer("プレイヤーデータの読み込みに失敗しました。".toErrorMessage))
            .runOnServerThread(Seichi915ServerCore.instance)
      }
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
  }
}
