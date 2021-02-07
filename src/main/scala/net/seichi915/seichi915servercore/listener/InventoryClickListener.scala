package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryType}
import org.bukkit.event.{EventHandler, EventPriority, Listener}
import org.bukkit.persistence.PersistentDataType

import java.util.UUID

class InventoryClickListener extends Listener {
  @EventHandler(priority = EventPriority.MONITOR)
  def onInventoryClick(event: InventoryClickEvent): Unit = {
    if (!event.getInventory.isSeichi915ServerInventory && event.getInventory.getType != InventoryType.CRAFTING)
      return
    if (event.getCurrentItem.isNull) return
    if (!event.getWhoClicked.isInstanceOf[Player]) return
    if (event.getCurrentItem.getItemMeta.isNull) return
    if (event.getCurrentItem.getItemMeta.getPersistentDataContainer.isNull)
      return
    if (!event.getCurrentItem.getItemMeta.getPersistentDataContainer.has(
          new NamespacedKey(Seichi915ServerCore.instance, "click_action"),
          PersistentDataType.STRING)) return
    val uuid = UUID.fromString(
      event.getCurrentItem.getItemMeta.getPersistentDataContainer.get(
        new NamespacedKey(Seichi915ServerCore.instance, "click_action"),
        PersistentDataType.STRING))
    val clickAction = Seichi915ServerCore.clickActionMap(uuid)
    clickAction.onClick(event.getWhoClicked.asInstanceOf[Player])
    event.setCancelled(true)
  }
}
