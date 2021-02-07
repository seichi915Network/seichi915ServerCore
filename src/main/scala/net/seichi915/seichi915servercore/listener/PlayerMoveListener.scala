package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.external.ExternalPlugins
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{Location, Material}
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.{EventHandler, Listener}

class PlayerMoveListener extends Listener {
  @EventHandler
  def onPlayerMove(event: PlayerMoveEvent): Unit = {
    val firstPosition = new Location(event.getTo.getWorld,
                                     event.getTo.getBlockX + 5,
                                     5,
                                     event.getTo.getBlockZ + 5)
    val secondPosition = new Location(event.getTo.getWorld,
                                      event.getTo.getBlockX - 5,
                                      5,
                                      event.getTo.getBlockZ - 5)
    for (y <- 0 until (firstPosition.getBlockY - secondPosition.getBlockY) + 1;
         z <- 0 until (firstPosition.getBlockZ - secondPosition.getBlockZ) + 1;
         x <- 0 until (firstPosition.getBlockX - secondPosition.getBlockX) + 1) {
      val block = event.getTo.getWorld.getBlockAt(
        new Location(event.getTo.getWorld,
                     firstPosition.getBlockX - x,
                     firstPosition.getBlockY - y,
                     firstPosition.getBlockZ - z))
      if (block.nonNull && block.getType == Material.AIR) {
        block.setType(Material.SMOOTH_STONE_SLAB)
        ExternalPlugins.getCoreProtectAPI.logPlacement(event.getPlayer.getName,
                                                       block.getLocation,
                                                       block.getType,
                                                       block.getBlockData)
      }
    }
  }
}
