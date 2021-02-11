package net.seichi915.seichi915servercore.menu

import cats.effect.IO
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.meta.menu.Menu
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.{ItemFlag, ItemStack}
import org.bukkit.inventory.meta.SkullMeta

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

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
          val itemStack = new ItemStack(Material.PLAYER_HEAD)
          val skullMeta = itemStack.getItemMeta.asInstanceOf[SkullMeta]
          skullMeta.setDisplayName(
            s"${ChatColor.AQUA}${count + 1}位: ${ChatColor.WHITE}${playerAndBreakAmount._1.getName}")
          skullMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
          skullMeta.setOwningPlayer(playerAndBreakAmount._1)
          skullMeta.setLore(List(
            s"${ChatColor.GREEN}総整地量: ${ChatColor.WHITE}${playerAndBreakAmount._2}").asJava)
          itemStack.setItemMeta(skullMeta)
          inventory.setItem(count, itemStack)
        }
      }
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunSync()
  }
}
