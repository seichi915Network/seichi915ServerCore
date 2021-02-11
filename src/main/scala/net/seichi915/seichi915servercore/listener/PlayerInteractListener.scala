package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.tooltype.ToolType._
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{GameMode, Material}
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, Listener}

class PlayerInteractListener extends Listener {
  @EventHandler
  def onPlayerInteract(event: PlayerInteractEvent): Unit = {
    if (event.getAction != Action.LEFT_CLICK_BLOCK) return
    if (event.getPlayer.getGameMode == GameMode.CREATIVE) return
    if (event.getPlayer.getInventory.getItemInMainHand.isNull || event.getPlayer.getInventory.getItemInMainHand.getType == Material.AIR)
      return
    event.getPlayer.getInventory.getItemInMainHand.setType(
      event.getClickedBlock.getUsableToolType match {
        case Pickaxe => Material.DIAMOND_PICKAXE
        case Shovel  => Material.DIAMOND_SHOVEL
        case Axe     => Material.DIAMOND_AXE
        case Sword   => Material.DIAMOND_SWORD
        case Shears  => Material.SHEARS
        case Hoe     => Material.DIAMOND_HOE
        case _       => event.getPlayer.getInventory.getItemInMainHand.getType
      }
    )
  }
}
