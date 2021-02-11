package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.event.PlayerSeichiEvent
import org.bukkit.event.{EventHandler, Listener}

class PlayerSeichiListener extends Listener {
  @EventHandler
  def onPlayerSeichi(event: PlayerSeichiEvent): Unit = {}
}
