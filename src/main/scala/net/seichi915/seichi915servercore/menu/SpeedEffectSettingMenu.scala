package net.seichi915.seichi915servercore.menu

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.inventory.{ItemFlag, ItemStack}

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

object SpeedEffectSettingMenu extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ServerInventoryHolder.seichi915ServerInventoryHolder,
      27,
      "移動速度上昇エフェクト設定")
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(player, {
        player.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    player.openInventory(inventory)
    val task = IO {
      val toggleEffectButton = new ItemStack(
        if (playerData.isSpeedEffectEnabled) Material.BLUE_WOOL
        else Material.RED_WOOL)
      val toggleEffectButtonMeta = toggleEffectButton.getItemMeta
      toggleEffectButtonMeta.setDisplayName(
        s"${if (playerData.isSpeedEffectEnabled) s"${ChatColor.AQUA}エフェクトは現在オンです"
        else s"${ChatColor.RED}エフェクトは現在オフです"}")
      toggleEffectButtonMeta.setLore(
        List(s"${ChatColor.WHITE}クリックでオン・オフを切り替えられます。").asJava)
      toggleEffectButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      toggleEffectButton.setItemMeta(toggleEffectButtonMeta)
      toggleEffectButton.setClickAction { player =>
        playerData.setSpeedEffectEnabled(!playerData.isSpeedEffectEnabled)
        player.playMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(4, toggleEffectButton)
      val raiseAmplifierBy1Button = new ItemStack(Material.PLAYER_HEAD)
      val raiseAmplifierBy1ButtonMeta =
        raiseAmplifierBy1Button.getItemMeta.asInstanceOf[SkullMeta]
      raiseAmplifierBy1ButtonMeta.setOwner("MHF_ARROWUP")
      raiseAmplifierBy1ButtonMeta.setDisplayName(s"${ChatColor.AQUA}レベルを1上げる")
      raiseAmplifierBy1ButtonMeta.setLore(
        List(
          s"${if (playerData.getSpeedEffectAmplifier <= 255)
            s"レベルを ${ChatColor.YELLOW}${playerData.getSpeedEffectAmplifier}${ChatColor.WHITE} から ${ChatColor.GREEN}${playerData.getSpeedEffectAmplifier + 1}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}レベルの上限は256です。"}",
          s"${if (playerData.getSpeedEffectAmplifier >= 256) s"${ChatColor.RED}これ以上上げられません。"
          else ""}"
        ).map(str => s"${ChatColor.WHITE}$str").asJava
      )
      raiseAmplifierBy1ButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      raiseAmplifierBy1Button.setItemMeta(raiseAmplifierBy1ButtonMeta)
      raiseAmplifierBy1Button.setClickAction { player =>
        if (!(playerData.getSpeedEffectAmplifier >= 256)) {
          playerData.setSpeedEffectAmplifier(
            playerData.getSpeedEffectAmplifier + 1)
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(11, raiseAmplifierBy1Button)
      val raiseAmplifierBy10Button = new ItemStack(Material.PLAYER_HEAD)
      val raiseAmplifierBy10ButtonMeta =
        raiseAmplifierBy10Button.getItemMeta.asInstanceOf[SkullMeta]
      raiseAmplifierBy10ButtonMeta.setOwner("MHF_ARROWUP")
      raiseAmplifierBy10ButtonMeta.setDisplayName(s"${ChatColor.AQUA}レベルを10上げる")
      raiseAmplifierBy10ButtonMeta.setLore(
        List(
          s"${if (playerData.getSpeedEffectAmplifier + 10 <= 256)
            s"レベルを ${ChatColor.YELLOW}${playerData.getSpeedEffectAmplifier}${ChatColor.WHITE} から ${ChatColor.GREEN}${playerData.getSpeedEffectAmplifier + 10}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}レベルの上限は256です。"}",
          s"${if (playerData.getSpeedEffectAmplifier + 10 > 256) s"${ChatColor.RED}これ以上上げられません。"
          else ""}"
        ).map(str => s"${ChatColor.WHITE}$str").asJava
      )
      raiseAmplifierBy10ButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      raiseAmplifierBy10Button.setItemMeta(raiseAmplifierBy10ButtonMeta)
      raiseAmplifierBy10Button.setClickAction { player =>
        if (!(playerData.getSpeedEffectAmplifier + 10 > 256)) {
          playerData.setSpeedEffectAmplifier(
            playerData.getSpeedEffectAmplifier + 10)
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(20, raiseAmplifierBy10Button)
      val lowerAmplifierBy1Button = new ItemStack(Material.PLAYER_HEAD)
      val lowerAmplifierBy1ButtonMeta =
        lowerAmplifierBy1Button.getItemMeta.asInstanceOf[SkullMeta]
      lowerAmplifierBy1ButtonMeta.setOwner("MHF_ARROWDOWN")
      lowerAmplifierBy1ButtonMeta.setDisplayName(s"${ChatColor.AQUA}レベルを1下げる")
      lowerAmplifierBy1ButtonMeta.setLore(
        List(
          s"${if (playerData.getSpeedEffectAmplifier > 1)
            s"レベルを ${ChatColor.YELLOW}${playerData.getSpeedEffectAmplifier}${ChatColor.WHITE} から ${ChatColor.GREEN}${playerData.getSpeedEffectAmplifier - 1}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}レベルの下限は1です。"}",
          s"${if (playerData.getSpeedEffectAmplifier <= 1) s"${ChatColor.RED}これ以上下げられません。"
          else ""}"
        ).map(str => s"${ChatColor.WHITE}$str").asJava
      )
      lowerAmplifierBy1ButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      lowerAmplifierBy1Button.setItemMeta(lowerAmplifierBy1ButtonMeta)
      lowerAmplifierBy1Button.setClickAction { player =>
        if (!(playerData.getSpeedEffectAmplifier <= 1)) {
          playerData.setSpeedEffectAmplifier(
            playerData.getSpeedEffectAmplifier - 1)
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(15, lowerAmplifierBy1Button)
      val lowerAmplifierBy10Button = new ItemStack(Material.PLAYER_HEAD)
      val lowerAmplifierBy10ButtonMeta =
        lowerAmplifierBy10Button.getItemMeta.asInstanceOf[SkullMeta]
      lowerAmplifierBy10ButtonMeta.setOwner("MHF_ARROWDOWN")
      lowerAmplifierBy10ButtonMeta.setDisplayName(s"${ChatColor.AQUA}レベルを10下げる")
      lowerAmplifierBy10ButtonMeta.setLore(
        List(
          s"${if (playerData.getSpeedEffectAmplifier - 10 >= 1)
            s"レベルを ${ChatColor.YELLOW}${playerData.getSpeedEffectAmplifier}${ChatColor.WHITE} から ${ChatColor.GREEN}${playerData.getSpeedEffectAmplifier - 10}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}レベルの下限は1です。"}",
          s"${if (playerData.getSpeedEffectAmplifier - 10 < 1) s"${ChatColor.RED}これ以上下げられません。"
          else ""}"
        ).map(str => s"${ChatColor.WHITE}$str").asJava
      )
      lowerAmplifierBy10ButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      lowerAmplifierBy10Button.setItemMeta(lowerAmplifierBy10ButtonMeta)
      lowerAmplifierBy10Button.setClickAction { player =>
        if (!(playerData.getSpeedEffectAmplifier - 10 < 1)) {
          playerData.setSpeedEffectAmplifier(
            playerData.getSpeedEffectAmplifier - 10)
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
