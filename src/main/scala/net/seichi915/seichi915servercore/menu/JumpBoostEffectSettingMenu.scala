package net.seichi915.seichi915servercore.menu

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.builder.ItemStackBuilder
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

import scala.concurrent.ExecutionContext

object JumpBoostEffectSettingMenu extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ServerInventoryHolder.seichi915ServerInventoryHolder,
      27,
      "跳躍力上昇エフェクト設定")
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(player, {
        player.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    player.openInventory(inventory)
    val task = IO {
      val toggleEffectButton = ItemStackBuilder(
        if (playerData.isJumpBoostEffectEnabled) Material.BLUE_WOOL
        else Material.RED_WOOL)
        .setDisplayName(
          s"${if (playerData.isJumpBoostEffectEnabled) s"${ChatColor.AQUA}エフェクトは現在オンです"
          else s"${ChatColor.RED}エフェクトは現在オフです"}")
        .addLore(s"${ChatColor.WHITE}クリックでオン・オフを切り替えられます。")
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      toggleEffectButton.setClickAction { player =>
        playerData.setJumpBoostEffectEnabled(
          !playerData.isJumpBoostEffectEnabled)
        player.playMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(4, toggleEffectButton)
      val raiseAmplifierBy1Button = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWUP")
        .setDisplayName(s"${ChatColor.AQUA}レベルを1上げる")
        .addLore(
          List(
            s"${if (playerData.getJumpBoostEffectAmplifier <= 255)
              s"レベルを ${ChatColor.YELLOW}${playerData.getJumpBoostEffectAmplifier}${ChatColor.WHITE} から ${ChatColor.GREEN}${playerData.getJumpBoostEffectAmplifier + 1}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}レベルの上限は256です。"}",
            s"${if (playerData.getJumpBoostEffectAmplifier >= 256) s"${ChatColor.RED}これ以上上げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      raiseAmplifierBy1Button.setClickAction { player =>
        if (!(playerData.getJumpBoostEffectAmplifier >= 256)) {
          playerData.setJumpBoostEffectAmplifier(
            playerData.getJumpBoostEffectAmplifier + 1)
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(11, raiseAmplifierBy1Button)
      val raiseAmplifierBy10Button = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWUP")
        .setDisplayName(s"${ChatColor.AQUA}レベルを10上げる")
        .addLore(
          List(
            s"${if (playerData.getJumpBoostEffectAmplifier + 10 <= 256)
              s"レベルを ${ChatColor.YELLOW}${playerData.getJumpBoostEffectAmplifier}${ChatColor.WHITE} から ${ChatColor.GREEN}${playerData.getJumpBoostEffectAmplifier + 10}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}レベルの上限は256です。"}",
            s"${if (playerData.getJumpBoostEffectAmplifier + 10 > 256) s"${ChatColor.RED}これ以上上げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      raiseAmplifierBy10Button.setClickAction { player =>
        if (!(playerData.getJumpBoostEffectAmplifier + 10 > 256)) {
          playerData.setJumpBoostEffectAmplifier(
            playerData.getJumpBoostEffectAmplifier + 10)
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(20, raiseAmplifierBy10Button)
      val lowerAmplifierBy1Button = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWDOWN")
        .setDisplayName(s"${ChatColor.AQUA}レベルを1下げる")
        .addLore(
          List(
            s"${if (playerData.getJumpBoostEffectAmplifier > 1)
              s"レベルを ${ChatColor.YELLOW}${playerData.getJumpBoostEffectAmplifier}${ChatColor.WHITE} から ${ChatColor.GREEN}${playerData.getJumpBoostEffectAmplifier - 1}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}レベルの下限は1です。"}",
            s"${if (playerData.getJumpBoostEffectAmplifier <= 1) s"${ChatColor.RED}これ以上下げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      lowerAmplifierBy1Button.setClickAction { player =>
        if (!(playerData.getJumpBoostEffectAmplifier <= 1)) {
          playerData.setJumpBoostEffectAmplifier(
            playerData.getJumpBoostEffectAmplifier - 1)
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(15, lowerAmplifierBy1Button)
      val lowerAmplifierBy10Button = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWDOWN")
        .setDisplayName(s"${ChatColor.AQUA}レベルを10下げる")
        .addLore(
          List(
            s"${if (playerData.getJumpBoostEffectAmplifier - 10 >= 1)
              s"レベルを ${ChatColor.YELLOW}${playerData.getJumpBoostEffectAmplifier}${ChatColor.WHITE} から ${ChatColor.GREEN}${playerData.getJumpBoostEffectAmplifier - 10}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}レベルの下限は1です。"}",
            s"${if (playerData.getJumpBoostEffectAmplifier - 10 < 1) s"${ChatColor.RED}これ以上下げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      lowerAmplifierBy10Button.setClickAction { player =>
        if (!(playerData.getJumpBoostEffectAmplifier - 10 < 1)) {
          playerData.setJumpBoostEffectAmplifier(
            playerData.getJumpBoostEffectAmplifier - 10)
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(24, lowerAmplifierBy10Button)
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
  }
}
