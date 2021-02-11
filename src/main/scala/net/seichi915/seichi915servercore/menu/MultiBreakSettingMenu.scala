package net.seichi915.seichi915servercore.menu

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import net.seichi915.seichi915servercore.multibreak.MultiBreak
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.inventory.meta.SkullMeta

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

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
      val toggleMultiBreakButton = new ItemStack(
        if (playerData.isMultiBreakEnabled) Material.BLUE_WOOL
        else Material.RED_WOOL)
      val toggleMultiBreakButtonMeta = toggleMultiBreakButton.getItemMeta
      toggleMultiBreakButtonMeta.setDisplayName(
        s"${if (playerData.isMultiBreakEnabled) s"${ChatColor.AQUA}マルチブレイクは現在オンです"
        else s"${ChatColor.RED}マルチブレイクは現在オフです"}")
      toggleMultiBreakButtonMeta.setLore(
        List(s"${ChatColor.WHITE}クリックでオン・オフを切り替えられます。").asJava)
      toggleMultiBreakButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      toggleMultiBreakButton.setItemMeta(toggleMultiBreakButtonMeta)
      toggleMultiBreakButton.setClickAction { _ =>
        playerData.setMultiBreakEnabled(!playerData.isMultiBreakEnabled)
        player.playMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(4, toggleMultiBreakButton)
      val increaseWidthButton = new ItemStack(Material.PLAYER_HEAD)
      val increaseWidthButtonMeta =
        increaseWidthButton.getItemMeta.asInstanceOf[SkullMeta]
      increaseWidthButtonMeta.setOwner("MHF_ARROWUP")
      increaseWidthButtonMeta.setDisplayName(s"${ChatColor.AQUA}横幅を1段階上げる")
      increaseWidthButtonMeta.setLore(
        List(
          s"${if (multiBreak.getWidth <= 15)
            s"${ChatColor.WHITE}横幅を ${ChatColor.YELLOW}${multiBreak.getWidth}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getWidth + 2}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}横幅の上限は17ブロックです。"}",
          s"${if (multiBreak.getWidth >= playerData.getMaxMultiBreakSize) s"${ChatColor.RED}これ以上上げられません。"
          else ""}"
        ).asJava)
      increaseWidthButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      increaseWidthButton.setItemMeta(increaseWidthButtonMeta)
      increaseWidthButton.setClickAction { player =>
        if (!(multiBreak.getWidth >= playerData.getMaxMultiBreakSize)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth + 2,
                       multiBreak.getHeight,
                       multiBreak.getDepth))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(11, increaseWidthButton)
      val deduceWidthButton = new ItemStack(Material.PLAYER_HEAD)
      val deduceWidthButtonMeta =
        deduceWidthButton.getItemMeta.asInstanceOf[SkullMeta]
      deduceWidthButtonMeta.setOwner("MHF_ARROWDOWN")
      deduceWidthButtonMeta.setDisplayName(s"${ChatColor.AQUA}横幅を1段階下げる")
      deduceWidthButtonMeta.setLore(
        List(
          s"${if (multiBreak.getWidth > 3)
            s"${ChatColor.WHITE}横幅を ${ChatColor.YELLOW}${multiBreak.getWidth}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getWidth - 2}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}横幅の下限は3ブロックです。"}",
          s"${if (multiBreak.getWidth <= 3) s"${ChatColor.RED}これ以上下げられません。"
          else ""}"
        ).asJava)
      deduceWidthButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      deduceWidthButton.setItemMeta(deduceWidthButtonMeta)
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
      val increaseHeightButton = new ItemStack(Material.PLAYER_HEAD)
      val increaseHeightButtonMeta =
        increaseHeightButton.getItemMeta.asInstanceOf[SkullMeta]
      increaseHeightButtonMeta.setOwner("MHF_ARROWUP")
      increaseHeightButtonMeta.setDisplayName(s"${ChatColor.AQUA}縦幅を1段階上げる")
      increaseHeightButtonMeta.setLore(
        List(
          s"${if (multiBreak.getHeight <= 15)
            s"${ChatColor.WHITE}縦幅を ${ChatColor.YELLOW}${multiBreak.getHeight}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getHeight + 2}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}縦幅の上限は17ブロックです。"}",
          s"${if (multiBreak.getHeight >= playerData.getMaxMultiBreakSize) s"${ChatColor.RED}これ以上上げられません。"
          else ""}"
        ).asJava)
      increaseHeightButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      increaseHeightButton.setItemMeta(increaseHeightButtonMeta)
      increaseHeightButton.setClickAction { player =>
        if (!(multiBreak.getHeight >= playerData.getMaxMultiBreakSize)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth,
                       multiBreak.getHeight + 2,
                       multiBreak.getDepth))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(13, increaseHeightButton)
      val deduceHeightButton = new ItemStack(Material.PLAYER_HEAD)
      val deduceHeightButtonMeta =
        deduceHeightButton.getItemMeta.asInstanceOf[SkullMeta]
      deduceHeightButtonMeta.setOwner("MHF_ARROWDOWN")
      deduceHeightButtonMeta.setDisplayName(s"${ChatColor.AQUA}縦幅を1段階下げる")
      deduceHeightButtonMeta.setLore(
        List(
          s"${if (multiBreak.getHeight > 3)
            s"${ChatColor.WHITE}縦幅を ${ChatColor.YELLOW}${multiBreak.getHeight}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getHeight - 2}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}縦幅の下限は3ブロックです。"}",
          s"${if (multiBreak.getHeight <= 3) s"${ChatColor.RED}これ以上下げられません。"
          else ""}"
        ).asJava)
      deduceHeightButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      deduceHeightButton.setItemMeta(deduceHeightButtonMeta)
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
      val increaseDepthButton = new ItemStack(Material.PLAYER_HEAD)
      val increaseDepthButtonMeta =
        increaseDepthButton.getItemMeta.asInstanceOf[SkullMeta]
      increaseDepthButtonMeta.setOwner("MHF_ARROWUP")
      increaseDepthButtonMeta.setDisplayName(s"${ChatColor.AQUA}奥行を1段階上げる")
      increaseDepthButtonMeta.setLore(
        List(
          s"${if (multiBreak.getDepth <= 15)
            s"${ChatColor.WHITE}奥行を ${ChatColor.YELLOW}${multiBreak.getDepth}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getDepth + 2}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}奥行の上限は17ブロックです。"}",
          s"${if (multiBreak.getDepth >= playerData.getMaxMultiBreakSize) s"${ChatColor.RED}これ以上上げられません。"
          else ""}"
        ).asJava)
      increaseDepthButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      increaseDepthButton.setItemMeta(increaseDepthButtonMeta)
      increaseDepthButton.setClickAction { player =>
        if (!(multiBreak.getDepth >= playerData.getMaxMultiBreakSize)) {
          playerData.setMultiBreak(
            MultiBreak(multiBreak.getWidth,
                       multiBreak.getHeight,
                       multiBreak.getDepth + 2))
          player.playMenuButtonClickSound()
        } else player.playDisabledMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(15, increaseDepthButton)
      val deduceDepthButton = new ItemStack(Material.PLAYER_HEAD)
      val deduceDepthButtonMeta =
        deduceDepthButton.getItemMeta.asInstanceOf[SkullMeta]
      deduceDepthButtonMeta.setOwner("MHF_ARROWDOWN")
      deduceDepthButtonMeta.setDisplayName(s"${ChatColor.AQUA}奥行を1段階下げる")
      deduceDepthButtonMeta.setLore(
        List(
          s"${if (multiBreak.getDepth > 3)
            s"${ChatColor.WHITE}奥行を ${ChatColor.YELLOW}${multiBreak.getDepth}${ChatColor.WHITE} から ${ChatColor.GREEN}${multiBreak.getDepth - 2}${ChatColor.WHITE} に設定します。"
          else s"${ChatColor.RED}奥行の下限は3ブロックです。"}",
          s"${if (multiBreak.getDepth <= 3) s"${ChatColor.RED}これ以上下げられません。"
          else ""}"
        ).asJava)
      deduceDepthButtonMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
      deduceDepthButton.setItemMeta(deduceDepthButtonMeta)
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
    IO.shift(contextShift).flatMap(_ => task).unsafeRunSync()
  }
}
