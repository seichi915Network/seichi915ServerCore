package net.seichi915.seichi915servercore.menu

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.builder.ItemStackBuilder
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.potion.{PotionEffect, PotionEffectType}

object ShopMenu extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ServerInventoryHolder.seichi915ServerInventoryHolder,
      27,
      "ショップ")
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(player, {
        player.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    val expBoost = ItemStackBuilder(Material.EXPERIENCE_BOTTLE)
      .setDisplayName(s"${ChatColor.AQUA}Expブースト")
      .addLore(
        List(
          s"30分の間、取得Exp量が1.2倍になります。",
          "",
          s"${ChatColor.GREEN}価格: ${ChatColor.WHITE}投票ポイント20",
          s"${if (!(playerData.getVotePoint >= 20)) s"${ChatColor.RED}投票ポイントが足りません。"
          else ""}"
        ).map(str => s"${ChatColor.WHITE}$str")
      )
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    expBoost.setClickAction { player =>
      if (playerData.getVotePoint >= 20 && !player.hasPotionEffect(
            PotionEffectType.LUCK)) {
        player.playMenuButtonClickSound()
        player.addPotionEffect(
          new PotionEffect(PotionEffectType.LUCK, 36000, 0, true, true))
        playerData.setVotePoint(playerData.getVotePoint - 20)
      } else player.playDisabledMenuButtonClickSound()
      open(player)
    }
    inventory.setItem(0, expBoost)
    val powerfulExpBoost = ItemStackBuilder(Material.EXPERIENCE_BOTTLE)
      .setDisplayName(s"${ChatColor.AQUA}強力なExpブースト")
      .addLore(
        List(
          s"30分の間、取得Exp量が1.5倍になります。",
          "",
          s"${ChatColor.GREEN}価格: ${ChatColor.WHITE}投票ポイント40",
          s"${if (!(playerData.getVotePoint >= 20)) s"${ChatColor.RED}投票ポイントが足りません。"
          else ""}"
        ).map(str => s"${ChatColor.WHITE}$str")
      )
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    powerfulExpBoost.setClickAction { player =>
      if (playerData.getVotePoint >= 40 && !player.hasPotionEffect(
            PotionEffectType.LUCK)) {
        player.playMenuButtonClickSound()
        player.addPotionEffect(
          new PotionEffect(PotionEffectType.LUCK, 36000, 1, true, true))
        playerData.setVotePoint(playerData.getVotePoint - 40)
      } else player.playDisabledMenuButtonClickSound()
      open(player)
    }
    inventory.setItem(1, powerfulExpBoost)
    val superPowerfulExpBoost = ItemStackBuilder(Material.EXPERIENCE_BOTTLE)
      .setDisplayName(s"${ChatColor.AQUA}超強力なExpブースト")
      .addLore(
        List(
          s"30分の間、取得Exp量が2.5倍になります。",
          "",
          s"${ChatColor.GREEN}価格: ${ChatColor.WHITE}投票ポイント100",
          s"${if (!(playerData.getVotePoint >= 20)) s"${ChatColor.RED}投票ポイントが足りません。"
          else ""}"
        ).map(str => s"${ChatColor.WHITE}$str")
      )
      .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
      .build
    superPowerfulExpBoost.setClickAction { player =>
      if (playerData.getVotePoint >= 100 && !player.hasPotionEffect(
            PotionEffectType.LUCK)) {
        player.playMenuButtonClickSound()
        player.addPotionEffect(
          new PotionEffect(PotionEffectType.LUCK, 36000, 2, true, true))
        playerData.setVotePoint(playerData.getVotePoint - 100)
      } else player.playDisabledMenuButtonClickSound()
      open(player)
    }
    inventory.setItem(2, superPowerfulExpBoost)
    player.openInventory(inventory)
  }
}
