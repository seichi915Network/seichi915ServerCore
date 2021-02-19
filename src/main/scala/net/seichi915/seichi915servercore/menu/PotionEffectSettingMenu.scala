package net.seichi915.seichi915servercore.menu

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.builder.ItemStackBuilder
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.enchantments.Enchantment
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

object PotionEffectSettingMenu extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ServerInventoryHolder.seichi915ServerInventoryHolder,
      27,
      "ポーションエフェクト設定")
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(player, {
        player.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    val openSpeedEffectSettingButton = ItemStackBuilder(Material.DIAMOND_BOOTS)
      .setDisplayName(s"${ChatColor.AQUA}移動速度上昇エフェクト設定を開く")
      .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
      .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
      .build
    openSpeedEffectSettingButton.setClickAction { player =>
      SpeedEffectSettingMenu.open(player)
      player.playMenuButtonClickSound()
    }
    inventory.setItem(10, openSpeedEffectSettingButton)
    val openHasteEffectSettingButton =
      ItemStackBuilder(Material.DIAMOND_PICKAXE)
        .setDisplayName(s"${ChatColor.AQUA}採掘速度上昇エフェクト設定を開く")
        .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
        .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
        .build
    openHasteEffectSettingButton.setClickAction { player =>
      HasteEffectSettingMenu.open(player)
      player.playMenuButtonClickSound()
    }
    inventory.setItem(12, openHasteEffectSettingButton)
    val openJumpBoostEffectSettingButton = ItemStackBuilder(Material.FEATHER)
      .setDisplayName(s"${ChatColor.AQUA}跳躍力上昇エフェクト設定を開く")
      .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
      .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
      .build
    openJumpBoostEffectSettingButton.setClickAction { player =>
      JumpBoostEffectSettingMenu.open(player)
      player.playMenuButtonClickSound()
    }
    inventory.setItem(14, openJumpBoostEffectSettingButton)
    val toggleNightVisionEffectButton = ItemStackBuilder(Material.LANTERN)
      .setDisplayName(s"${ChatColor.AQUA}暗視の効果を切り替える")
      .addLore(
        List(
          s"現在 ${if (playerData.isNightVisionEffectEnabled) s"${ChatColor.GREEN}オン"
          else s"${ChatColor.RED}オフ"} ${ChatColor.WHITE}になっています。",
          "クリックでオン・オフを切り替えられます。"
        ).map(str => s"${ChatColor.WHITE}$str")
      )
      .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
      .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
      .build
    toggleNightVisionEffectButton.setClickAction { player =>
      playerData.setNightVisionEffectEnabled(
        !playerData.isNightVisionEffectEnabled)
      player.playMenuButtonClickSound()
      open(player)
    }
    inventory.setItem(16, toggleNightVisionEffectButton)
    player.openInventory(inventory)
  }
}
