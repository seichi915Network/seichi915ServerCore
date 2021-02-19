package net.seichi915.seichi915servercore.menu

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.builder.ItemStackBuilder
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import net.seichi915.seichi915servercore.multibreak.MultiBreak
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

import scala.concurrent.ExecutionContext

object MultiBreakSettingMenu extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ServerInventoryHolder.seichi915ServerInventoryHolder,
      27,
      "マルチブレイク設定")
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(player, {
        player.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    val multiBreak = playerData.getMultiBreak
    player.openInventory(inventory)
    val task = IO {
      val toggleMultiBreakButton = ItemStackBuilder(
        if (playerData.isMultiBreakEnabled) Material.BLUE_WOOL
        else Material.RED_WOOL)
        .setDisplayName(
          s"${if (playerData.isMultiBreakEnabled) s"${ChatColor.AQUA}マルチブレイクは現在オンです"
          else s"${ChatColor.RED}マルチブレイクは現在オフです"}")
        .addLore(s"${ChatColor.WHITE}クリックでオン・オフを切り替えられます。")
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      toggleMultiBreakButton.setClickAction { _ =>
        playerData.setMultiBreakEnabled(!playerData.isMultiBreakEnabled)
        player.playMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(4, toggleMultiBreakButton)
      val increaseWidthButton = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWUP")
        .setDisplayName(s"${ChatColor.AQUA}横幅を1段階上げる")
        .addLore(
          List(
            s"${if (multiBreak.getWidth <= 15)
              s"横幅を ${ChatColor.YELLOW}${multiBreak.getWidth}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getWidth + 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}横幅の上限は17ブロックです。"}",
            s"${if (multiBreak.getWidth >= playerData.calcMaxMultiBreakSize) s"${ChatColor.RED}これ以上上げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      increaseWidthButton.setClickAction { player =>
        if (!(multiBreak.getWidth >= playerData.calcMaxMultiBreakSize)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth + 2,
                       multiBreak.getHeight,
                       multiBreak.getDepth))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(11, increaseWidthButton)
      val deduceWidthButton = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWDOWN")
        .setDisplayName(s"${ChatColor.AQUA}横幅を1段階下げる")
        .addLore(
          List(
            s"${if (multiBreak.getWidth > 3)
              s"横幅を ${ChatColor.YELLOW}${multiBreak.getWidth}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getWidth - 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}横幅の下限は3ブロックです。"}",
            s"${if (multiBreak.getWidth <= 3) s"${ChatColor.RED}これ以上下げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      deduceWidthButton.setClickAction { player =>
        if (!(multiBreak.getWidth <= 3)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth - 2,
                       multiBreak.getHeight,
                       multiBreak.getDepth))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(20, deduceWidthButton)
      val increaseHeightButton = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWUP")
        .setDisplayName(s"${ChatColor.AQUA}縦幅を1段階上げる")
        .addLore(
          List(
            s"${if (multiBreak.getHeight <= 15)
              s"縦幅を ${ChatColor.YELLOW}${multiBreak.getHeight}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getHeight + 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}縦幅の上限は17ブロックです。"}",
            s"${if (multiBreak.getHeight >= playerData.calcMaxMultiBreakSize) s"${ChatColor.RED}これ以上上げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      increaseHeightButton.setClickAction { player =>
        if (!(multiBreak.getHeight >= playerData.calcMaxMultiBreakSize)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth,
                       multiBreak.getHeight + 2,
                       multiBreak.getDepth))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(13, increaseHeightButton)
      val deduceHeightButton = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWDOWN")
        .setDisplayName(s"${ChatColor.AQUA}縦幅を1段階下げる")
        .addLore(
          List(
            s"${if (multiBreak.getHeight > 3)
              s"縦幅を ${ChatColor.YELLOW}${multiBreak.getHeight}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getHeight - 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}縦幅の下限は3ブロックです。"}",
            s"${if (multiBreak.getHeight <= 3) s"${ChatColor.RED}これ以上下げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      deduceHeightButton.setClickAction { player =>
        if (!(multiBreak.getHeight <= 3)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth,
                       multiBreak.getHeight - 2,
                       multiBreak.getDepth))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(22, deduceHeightButton)
      val increaseDepthButton = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWUP")
        .setDisplayName(s"${ChatColor.AQUA}奥行を1段階上げる")
        .addLore(
          List(
            s"${if (multiBreak.getDepth <= 15)
              s"奥行を ${ChatColor.YELLOW}${multiBreak.getDepth}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getDepth + 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}奥行の上限は17ブロックです。"}",
            s"${if (multiBreak.getDepth >= playerData.calcMaxMultiBreakSize) s"${ChatColor.RED}これ以上上げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      increaseDepthButton.setClickAction { player =>
        if (!(multiBreak.getDepth >= playerData.calcMaxMultiBreakSize)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth,
                       multiBreak.getHeight,
                       multiBreak.getDepth + 2))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(15, increaseDepthButton)
      val deduceDepthButton = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWDOWN")
        .setDisplayName(s"${ChatColor.AQUA}奥行を1段階下げる")
        .addLore(
          List(
            s"${if (multiBreak.getDepth > 3)
              s"奥行を ${ChatColor.YELLOW}${multiBreak.getDepth}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getDepth - 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}奥行の下限は3ブロックです。"}",
            s"${if (multiBreak.getDepth <= 3) s"${ChatColor.RED}これ以上下げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      deduceDepthButton.setClickAction { player =>
        if (!(multiBreak.getDepth <= 3)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth,
                       multiBreak.getHeight,
                       multiBreak.getDepth - 2))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(24, deduceDepthButton)
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
  }
}
