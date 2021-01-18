package net.seichi915.seichi915servercore.inventory

import org.bukkit.inventory.{Inventory, InventoryHolder}

object Seichi915ServerInventoryHolder {
  val seichi915ServerInventoryHolder: Seichi915ServerInventoryHolder =
    new Seichi915ServerInventoryHolder {
      override def getInventory: Inventory = null
    }
}

trait Seichi915ServerInventoryHolder extends InventoryHolder
