package net.seichi915.seichi915servercore.util

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.menu.ClickAction
import org.bukkit.NamespacedKey
import org.bukkit.inventory.{Inventory, ItemStack}
import org.bukkit.persistence.PersistentDataType

import java.util.UUID

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

}
