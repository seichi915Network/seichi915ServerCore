package net.seichi915.seichi915servercore.listener

import org.bukkit.GameMode
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.{EventHandler, Listener}

class PlayerSwapHandItemsListener extends Listener {
  @EventHandler
  def onPlayerSwapItem(event: PlayerSwapHandItemsEvent): Unit =
    if (event.getPlayer.getGameMode != GameMode.CREATIVE)
      event.setCancelled(true)
}
