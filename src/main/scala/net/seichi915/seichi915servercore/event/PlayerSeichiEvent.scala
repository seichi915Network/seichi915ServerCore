package net.seichi915.seichi915servercore.event

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.{Cancellable, HandlerList}
import org.bukkit.event.player.PlayerEvent

object PlayerSeichiEvent {
  private val handlers: HandlerList = new HandlerList
}

class PlayerSeichiEvent(who: Player, block: Block)
    extends PlayerEvent(who)
    with Cancellable {
  private var cancelled = false

  def getBlock: Block = block

  override def isCancelled: Boolean = cancelled

  override def setCancelled(cancel: Boolean): Unit = cancelled = cancel

  override def getHandlers: HandlerList = PlayerSeichiEvent.handlers
}
