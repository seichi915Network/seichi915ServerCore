package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.external.ExternalPlugins
import net.seichi915.seichi915servercore.util.Implicits._
import net.seichi915.seichi915servercore.util.Util
import org.bukkit.{GameMode, Location, Material}
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.{EventHandler, Listener}

class PlayerMoveListener extends Listener {
  @EventHandler
  def onPlayerMove(event: PlayerMoveEvent): Unit = {
    if (event.getPlayer.getGameMode == GameMode.CREATIVE) return
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(event.getPlayer, {
        event.getPlayer.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    if (event.getPlayer.getLocation.getBlockY <= 4)
      while (event.getPlayer.getWorld
               .getBlockAt(event.getPlayer.getLocation)
               .getType != Material.AIR || event.getPlayer.getWorld
               .getBlockAt(event.getPlayer.getLocation.clone().add(0, 1, 0))
               .getType != Material.AIR) event.getPlayer.teleport(
        event.getPlayer.getLocation.clone().add(0, 1, 0))
    if (playerData.isLiquidHardenerEnabled)
      Util
        .calcTargetBlocks(event.getPlayer,
                          event.getPlayer.getTargetBlock(1),
                          playerData.getLiquidHardener)
        .foreach { block =>
          block.getType match {
            case Material.LAVA =>
              ExternalPlugins.getCoreProtectAPI.logRemoval(
                event.getPlayer.getName,
                block.getLocation,
                block.getType,
                block.getBlockData)
              block.setType(Material.MAGMA_BLOCK)
              ExternalPlugins.getCoreProtectAPI.logPlacement(
                event.getPlayer.getName,
                block.getLocation,
                block.getType,
                block.getBlockData)
            case Material.WATER =>
              ExternalPlugins.getCoreProtectAPI.logRemoval(
                event.getPlayer.getName,
                block.getLocation,
                block.getType,
                block.getBlockData)
              block.setType(Material.PACKED_ICE)
              ExternalPlugins.getCoreProtectAPI.logPlacement(
                event.getPlayer.getName,
                block.getLocation,
                block.getType,
                block.getBlockData)
            case _ =>
          }
        }
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
      if (block.getType != Material.SMOOTH_STONE_SLAB) {
        if (block.getType != Material.AIR)
          ExternalPlugins.getCoreProtectAPI.logRemoval(event.getPlayer.getName,
                                                       block.getLocation,
                                                       block.getType,
                                                       block.getBlockData)
        block.setType(Material.SMOOTH_STONE_SLAB)
        ExternalPlugins.getCoreProtectAPI.logPlacement(event.getPlayer.getName,
                                                       block.getLocation,
                                                       block.getType,
                                                       block.getBlockData)
      }
    }
  }
}
