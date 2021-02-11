package net.seichi915.seichi915servercore.listener

import org.bukkit.GameMode
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.{EventHandler, Listener}

class PlayerDropItemListener extends Listener {
  @EventHandler
  def onPlayerDropItem(event: PlayerDropItemEvent): Unit =
    if (event.getPlayer.getGameMode != GameMode.CREATIVE)
      event.setCancelled(true)
}
