package net.seichi915.seichi915servercore.menu

import cats.effect.IO
import net.seichi915.seichi915servercore.builder.ItemStackBuilder
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

import scala.concurrent.ExecutionContext

object BreakAmountRankingMenu extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915ServerInventoryHolder.seichi915ServerInventoryHolder,
      54,
      "総整地量ランキング(上位54名)")
    player.openInventory(inventory)
    val task = IO {
      val playerAndBreakAmounts =
        Database.getPlayerAndBreakAmount.sortBy(_._2).reverse
      (0 to 53).foreach { count =>
        if (playerAndBreakAmounts.length > count) {
          val playerAndBreakAmount = playerAndBreakAmounts(count)
          val itemStack = ItemStackBuilder(Material.PLAYER_HEAD)
            .setDisplayName(
              s"${ChatColor.AQUA}${count + 1}位: ${ChatColor.WHITE}${Database
                .getName(playerAndBreakAmount._1.getUniqueId)
                .getOrElse(s"${ChatColor.RED}${playerAndBreakAmount._1.getUniqueId.toString}")}")
            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
            .setSkullOwner(playerAndBreakAmount._1)
            .addLore(
              s"${ChatColor.GREEN}総整地量: ${ChatColor.WHITE}${playerAndBreakAmount._2}",
              "",
              s"${ChatColor.GREEN}Rank: ${ChatColor.WHITE}${Database.getRank(playerAndBreakAmount._1.getUniqueId).getOrElse(1)}",
              s"${ChatColor.GREEN}Exp: ${ChatColor.WHITE}${Database.getExp(playerAndBreakAmount._1.getUniqueId).getOrElse(0.0)}"
            )
            .build
          inventory.setItem(count, itemStack)
        }
      }
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
  }
}
