package net.seichi915.seichi915servercore.builder

import org.bukkit.{Material, OfflinePlayer}
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.inventory.{ItemFlag, ItemStack}

import scala.jdk.CollectionConverters._

case class ItemStackBuilder(material: Material,
                            displayName: Option[String],
                            lore: List[String],
                            skullOwner: Option[AnyRef],
                            var enchantments: Map[Enchantment, Int],
                            itemFlags: List[ItemFlag]) {
  def setMaterial(material: Material): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     lore,
                     skullOwner,
                     enchantments,
                     itemFlags)

  def setDisplayName(displayName: String): ItemStackBuilder =
    ItemStackBuilder(material,
                     Some(displayName),
                     lore,
                     skullOwner,
                     enchantments,
                     itemFlags)

  def addLore(lore: String): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     this.lore.appended(lore),
                     skullOwner,
                     enchantments,
                     itemFlags)

  def addLore(lore: List[String]): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     this.lore.appendedAll(lore),
                     skullOwner,
                     enchantments,
                     itemFlags)

  def addLore(lore: String*): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     this.lore.appendedAll(lore),
                     skullOwner,
                     enchantments,
                     itemFlags)

  def setSkullOwner(offlinePlayer: OfflinePlayer): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     lore,
                     Some(offlinePlayer),
                     enchantments,
                     itemFlags)

  def setSkullOwner(name: String): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     lore,
                     Some(name),
                     enchantments,
                     itemFlags)

  def addEnchantment(enchantment: Enchantment, level: Int): ItemStackBuilder = {
    enchantments += enchantment -> level
    ItemStackBuilder(material,
                     displayName,
                     lore,
                     skullOwner,
                     enchantments,
                     itemFlags)
  }

  def addItemFlag(itemFlag: ItemFlag): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     lore,
                     skullOwner,
                     enchantments,
                     itemFlags.appended(itemFlag))

  def addItemFlags(itemFlags: List[ItemFlag]): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     lore,
                     skullOwner,
                     enchantments,
                     this.itemFlags.appendedAll(itemFlags))

  def addItemFlags(itemFlags: ItemFlag*): ItemStackBuilder =
    ItemStackBuilder(material,
                     displayName,
                     lore,
                     skullOwner,
                     enchantments,
                     this.itemFlags.appendedAll(itemFlags))

  def build: ItemStack = {
    val itemStack = new ItemStack(material)
    if (material == Material.PLAYER_HEAD && skullOwner.nonEmpty) {
      val skullMeta = itemStack.getItemMeta.asInstanceOf[SkullMeta]
      skullOwner.get match {
        case offlinePlayer: OfflinePlayer =>
          skullMeta.setOwningPlayer(offlinePlayer)
        case string: String =>
          skullMeta.setOwner(string)
      }
      itemStack.setItemMeta(skullMeta)
    }
    val itemMeta = itemStack.getItemMeta
    displayName match {
      case Some(name) => itemMeta.setDisplayName(name)
      case None       =>
    }
    itemMeta.setLore(lore.asJava)
    itemMeta.addItemFlags(itemFlags: _*)
    itemStack.setItemMeta(itemMeta)
    enchantments.foreach {
      case (enchantment: Enchantment, level: Int) =>
        itemStack.addUnsafeEnchantment(enchantment, level)
    }
    itemStack
  }
}
