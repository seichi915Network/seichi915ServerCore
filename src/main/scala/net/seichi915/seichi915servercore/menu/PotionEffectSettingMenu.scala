package net.seichi915.seichi915servercore.menu

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.enchantments.Enchantment
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.{ItemFlag, ItemStack}

import scala.jdk.CollectionConverters._

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
    val openSpeedEffectSettingButton = new ItemStack(Material.DIAMOND_BOOTS)
    val openSpeedEffectSettingButtonMeta =
      openSpeedEffectSettingButton.getItemMeta
    openSpeedEffectSettingButtonMeta.setDisplayName(
      s"${ChatColor.AQUA}移動速度上昇エフェクト設定を開く")
    openSpeedEffectSettingButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                                                  ItemFlag.HIDE_ENCHANTS)
    openSpeedEffectSettingButton.setItemMeta(openSpeedEffectSettingButtonMeta)
    openSpeedEffectSettingButton.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE,
                                                      1)
    openSpeedEffectSettingButton.setClickAction { player =>
      SpeedEffectSettingMenu.open(player)
      player.playMenuButtonClickSound()
    }
    inventory.setItem(10, openSpeedEffectSettingButton)
    val openHasteEffectSettingButton = new ItemStack(Material.DIAMOND_PICKAXE)
    val openHasteEffectSettingButtonMeta =
      openHasteEffectSettingButton.getItemMeta
    openHasteEffectSettingButtonMeta.setDisplayName(
      s"${ChatColor.AQUA}採掘速度上昇エフェクト設定を開く")
    openHasteEffectSettingButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                                                  ItemFlag.HIDE_ENCHANTS)
    openHasteEffectSettingButton.setItemMeta(openHasteEffectSettingButtonMeta)
    openHasteEffectSettingButton.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE,
                                                      1)
    openHasteEffectSettingButton.setClickAction { player =>
      HasteEffectSettingMenu.open(player)
      player.playMenuButtonClickSound()
    }
    inventory.setItem(12, openHasteEffectSettingButton)
    val openJumpBoostEffectSettingButton = new ItemStack(Material.FEATHER)
    val openJumpBoostEffectSettingButtonMeta =
      openJumpBoostEffectSettingButton.getItemMeta
    openJumpBoostEffectSettingButtonMeta.setDisplayName(
      s"${ChatColor.AQUA}跳躍力上昇エフェクト設定を開く")
    openJumpBoostEffectSettingButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                                                      ItemFlag.HIDE_ENCHANTS)
    openJumpBoostEffectSettingButton.setItemMeta(
      openJumpBoostEffectSettingButtonMeta)
    openJumpBoostEffectSettingButton.addUnsafeEnchantment(
      Enchantment.ARROW_DAMAGE,
      1)
    openJumpBoostEffectSettingButton.setClickAction { player =>
      JumpBoostEffectSettingMenu.open(player)
      player.playMenuButtonClickSound()
    }
    inventory.setItem(14, openJumpBoostEffectSettingButton)
    val toggleNightVisionEffectButton = new ItemStack(Material.LANTERN)
    val toggleNightVisionEffectButtonMeta =
      toggleNightVisionEffectButton.getItemMeta
    toggleNightVisionEffectButtonMeta.setDisplayName(
      s"${ChatColor.AQUA}暗視の効果を切り替える")
    toggleNightVisionEffectButtonMeta.setLore(List(
      s"現在 ${if (playerData.isNightVisionEffectEnabled) s"${ChatColor.GREEN}オン"
      else s"${ChatColor.RED}オフ"} ${ChatColor.WHITE}になっています。",
      "クリックでオン・オフを切り替えられます。").map(str => s"${ChatColor.WHITE}$str").asJava)
    toggleNightVisionEffectButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                                                   ItemFlag.HIDE_ENCHANTS)
    toggleNightVisionEffectButton.setItemMeta(toggleNightVisionEffectButtonMeta)
    toggleNightVisionEffectButton.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE,
                                                       1)
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
