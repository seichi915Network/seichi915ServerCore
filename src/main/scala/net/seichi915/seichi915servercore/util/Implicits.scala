package net.seichi915.seichi915servercore.util

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.menu.ClickAction
import net.seichi915.seichi915servercore.playerdata.PlayerData
import org.bukkit.{ChatColor, NamespacedKey}
import org.bukkit.entity.Player
import org.bukkit.inventory.{Inventory, ItemStack}
import org.bukkit.persistence.PersistentDataType

import java.util.UUID
import scala.concurrent.Future

object Implicits {
  implicit class AnyOps(any: Any) {
    def isNull: Boolean = Option(any).flatMap(_ => Some(false)).getOrElse(true)

    def nonNull: Boolean = !isNull
  }

  implicit class InventoryOps(inventory: Inventory) {
    def isSeichi915ServerInventory: Boolean =
      inventory.getHolder.nonNull && inventory.getHolder
        .isInstanceOf[Seichi915ServerInventoryHolder]
  }

  implicit class ItemStackOps(itemStack: ItemStack) {
    def setClickAction(clickAction: ClickAction): Unit = {
      val itemMeta = itemStack.getItemMeta
      val uuid = UUID.randomUUID()
      itemMeta.getPersistentDataContainer.set(
        new NamespacedKey(Seichi915ServerCore.instance, "click_action"),
        PersistentDataType.STRING,
        uuid.toString)
      itemStack.setItemMeta(itemMeta)
      Seichi915ServerCore.clickActionMap += uuid -> clickAction
    }
  }

  implicit class PlayerOps(player: Player) {
    def getPlayerData: Future[Option[PlayerData]] =
      Database.getPlayerData(player)

    def createNewPlayerData: Future[Unit] = Database.createNewPlayerData(player)
  }

  implicit class StringOps(string: String) {
    def toNormalMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.WHITE}seichi915Server${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toSuccessMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.GREEN}seichi915Server${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toWarningMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.GOLD}seichi915Server${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toErrorMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.RED}seichi915Server${ChatColor.AQUA}]${ChatColor.RESET} $string"
  }

}
