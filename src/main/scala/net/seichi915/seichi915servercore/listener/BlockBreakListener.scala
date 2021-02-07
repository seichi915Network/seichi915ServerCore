package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.external.ExternalPlugins
import net.seichi915.seichi915servercore.util.Implicits._
import net.seichi915.seichi915servercore.util.Util
import org.bukkit.{ChatColor, Material}
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.{EventHandler, Listener}

class BlockBreakListener extends Listener {
  @EventHandler
  def onBlockBreak(event: BlockBreakEvent): Unit = {
    event.setCancelled(true)
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(event.getPlayer, {
        event.getPlayer.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    if (event.getBlock.getLocation.getBlockY <= 5 && event.getBlock.getType == Material.SMOOTH_STONE_SLAB)
      return
    val targetBlocks =
      if (playerData.isMultiBreakEnabled)
        Util.calcTargetBlocks(event.getPlayer,
                              event.getBlock,
                              playerData.getMultiBreak)
      else List(event.getBlock)
    targetBlocks.foreach { block =>
      ExternalPlugins.getCoreProtectAPI.logRemoval(event.getPlayer.getName,
                                                   block.getLocation,
                                                   block.getType,
                                                   block.getBlockData)
      block.setType(Material.AIR)
      val previousMaxMultiBreakSize = playerData.getMaxMultiBreakSize
      playerData.setTotalBreakAmount(playerData.getTotalBreakAmount + 1)
      if (playerData.getMaxMultiBreakSize != previousMaxMultiBreakSize) {
        event.getPlayer.sendMessage(
          s"マルチブレイクのサイズ上限が ${ChatColor.GREEN}${playerData.getMaxMultiBreakSize} ${ChatColor.RESET}になりました。".toSuccessMessage)
        event.getPlayer.playChangeMaxMultiBreakSizeSound()
      }
    }
    Seichi915ServerCore.bossBarMap
      .getOrElse(event.getPlayer, return )
      .setTitle(s"総整地量: ${playerData.getTotalBreakAmount}")
  }
}
