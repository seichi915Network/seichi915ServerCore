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

object LiquidHardenerSettingMenu extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ServerInventoryHolder.seichi915ServerInventoryHolder,
      27,
      "液体絶対固めるマン設定")
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(player, {
        player.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    val liquidHardener = playerData.getLiquidHardener
    player.openInventory(inventory)
    val task = IO {
      val toggleLiquidHardenerButton = ItemStackBuilder(
        if (playerData.isLiquidHardenerEnabled) Material.BLUE_WOOL
        else Material.RED_WOOL)
        .setDisplayName(
          s"${if (playerData.isLiquidHardenerEnabled) s"${ChatColor.AQUA}液体絶対固めるマンは現在オンです"
          else s"${ChatColor.RED}液体絶対固めるマンは現在オフです"}")
        .addLore(s"${ChatColor.WHITE}クリックでオン・オフを切り替えられます。")
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      toggleLiquidHardenerButton.setClickAction { _ =>
        playerData.setLiquidHardenerEnabled(!playerData.isLiquidHardenerEnabled)
        player.playMenuButtonClickSound()
        open(player)
      }
      inventory.setItem(4, toggleLiquidHardenerButton)
      val increaseWidthButton = ItemStackBuilder(Material.PLAYER_HEAD)
        .setSkullOwner("MHF_ARROWUP")
        .setDisplayName(s"${ChatColor.AQUA}横幅を1段階上げる")
        .addLore(
          List(
            s"${if (liquidHardener.getWidth <= 15)
              s"横幅を ${ChatColor.YELLOW}${liquidHardener.getWidth}${ChatColor.WHITE} から ${ChatColor.GREEN}${liquidHardener.getWidth + 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}横幅の上限は17ブロックです。"}",
            s"${if (liquidHardener.getWidth >= playerData.calcMaxMultiBreakSize)
              s"${ChatColor.RED}これ以上上げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      increaseWidthButton.setClickAction { player =>
        if (!(liquidHardener.getWidth >= playerData.calcMaxMultiBreakSize)) {
          playerData.setLiquidHardener(
            MultiBreak(liquidHardener.getWidth + 2,
                       liquidHardener.getHeight,
                       liquidHardener.getDepth))
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
            s"${if (liquidHardener.getWidth > 3)
              s"横幅を ${ChatColor.YELLOW}${liquidHardener.getWidth}${ChatColor.WHITE} から ${ChatColor.GREEN}${liquidHardener.getWidth - 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}横幅の下限は3ブロックです。"}",
            s"${if (liquidHardener.getWidth <= 3) s"${ChatColor.RED}これ以上下げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      deduceWidthButton.setClickAction { player =>
        if (!(liquidHardener.getWidth <= 3)) {
          playerData.setLiquidHardener(
            MultiBreak(liquidHardener.getWidth - 2,
                       liquidHardener.getHeight,
                       liquidHardener.getDepth))
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
            s"${if (liquidHardener.getHeight <= 15)
              s"縦幅を ${ChatColor.YELLOW}${liquidHardener.getHeight}${ChatColor.WHITE} から ${ChatColor.GREEN}${liquidHardener.getHeight + 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}縦幅の上限は17ブロックです。"}",
            s"${if (liquidHardener.getHeight >= playerData.calcMaxMultiBreakSize)
              s"${ChatColor.RED}これ以上上げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      increaseHeightButton.setClickAction { player =>
        if (!(liquidHardener.getHeight >= playerData.calcMaxMultiBreakSize)) {
          playerData.setLiquidHardener(
            MultiBreak(liquidHardener.getWidth,
                       liquidHardener.getHeight + 2,
                       liquidHardener.getDepth))
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
            s"${if (liquidHardener.getHeight > 3)
              s"縦幅を ${ChatColor.YELLOW}${liquidHardener.getHeight}${ChatColor.WHITE} から ${ChatColor.GREEN}${liquidHardener.getHeight - 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}縦幅の下限は3ブロックです。"}",
            s"${if (liquidHardener.getHeight <= 3) s"${ChatColor.RED}これ以上下げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      deduceHeightButton.setClickAction { player =>
        if (!(liquidHardener.getHeight <= 3)) {
          playerData.setLiquidHardener(
            MultiBreak(liquidHardener.getWidth,
                       liquidHardener.getHeight - 2,
                       liquidHardener.getDepth))
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
            s"${if (liquidHardener.getDepth <= 15)
              s"奥行を ${ChatColor.YELLOW}${liquidHardener.getDepth}${ChatColor.WHITE} から ${ChatColor.GREEN}${liquidHardener.getDepth + 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}奥行の上限は17ブロックです。"}",
            s"${if (liquidHardener.getDepth >= playerData.calcMaxMultiBreakSize)
              s"${ChatColor.RED}これ以上上げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      increaseDepthButton.setClickAction { player =>
        if (!(liquidHardener.getDepth >= playerData.calcMaxMultiBreakSize)) {
          playerData.setLiquidHardener(
            MultiBreak(liquidHardener.getWidth,
                       liquidHardener.getHeight,
                       liquidHardener.getDepth + 2))
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
            s"${if (liquidHardener.getDepth > 3)
              s"奥行を ${ChatColor.YELLOW}${liquidHardener.getDepth}${ChatColor.WHITE} から ${ChatColor.GREEN}${liquidHardener.getDepth - 2}${ChatColor.WHITE} に設定します。"
            else s"${ChatColor.RED}奥行の下限は3ブロックです。"}",
            s"${if (liquidHardener.getDepth <= 3) s"${ChatColor.RED}これ以上下げられません。"
            else ""}"
          ).map(str => s"${ChatColor.WHITE}$str")
        )
        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
        .build
      deduceDepthButton.setClickAction { player =>
        if (!(liquidHardener.getDepth <= 3)) {
          playerData.setLiquidHardener(
            MultiBreak(liquidHardener.getWidth,
                       liquidHardener.getHeight,
                       liquidHardener.getDepth - 2))
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
