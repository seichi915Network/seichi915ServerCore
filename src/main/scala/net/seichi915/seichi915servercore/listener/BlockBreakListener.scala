package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.event.PlayerSeichiEvent
import net.seichi915.seichi915servercore.external.ExternalPlugins
import net.seichi915.seichi915servercore.util.Implicits._
import net.seichi915.seichi915servercore.util.Util
import org.bukkit.{Bukkit, ChatColor, GameMode, Location, Material}
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.{EventHandler, Listener}

class BlockBreakListener extends Listener {
  private val tools = List(
    Material.DIAMOND_PICKAXE,
    Material.DIAMOND_SHOVEL,
    Material.DIAMOND_AXE,
    Material.DIAMOND_SWORD,
    Material.SHEARS,
    Material.DIAMOND_HOE
  )

  @EventHandler
  def onBlockBreak(event: BlockBreakEvent): Unit = {
    if (event.getPlayer.getGameMode == GameMode.CREATIVE) return
    event.setCancelled(true)
    if (!tools.contains(event.getPlayer.getInventory.getItemInMainHand.getType)) {
      event.getPlayer.sendActionBar("ツールを使用して掘ってください。")
      return
    }
    var blockCount = 0
    for (i <- event.getBlock.getLocation.getBlockY + 1 until 256) {
      val location = new Location(event.getBlock.getWorld,
                                  event.getBlock.getLocation.getBlockX,
                                  i,
                                  event.getBlock.getLocation.getBlockZ)
      if (event.getBlock.getWorld.getBlockAt(location).getType != Material.AIR)
        blockCount += 1
    }
    if (blockCount >= 20) {
      event.getPlayer.sendActionBar("上から掘ってください。")
      return
    }
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
    var totalExp = BigDecimal(0.0)
    targetBlocks.foreach { block =>
      val playerSeichiEvent = new PlayerSeichiEvent(event.getPlayer, block)
      Bukkit.getPluginManager.callEvent(playerSeichiEvent)
      if (!playerSeichiEvent.isCancelled) {
        ExternalPlugins.getCoreProtectAPI.logRemoval(event.getPlayer.getName,
                                                     block.getLocation,
                                                     block.getType,
                                                     block.getBlockData)
        block.setType(Material.AIR)
        playerData.setTotalBreakAmount(playerData.getTotalBreakAmount + 1)
        if (playerData.calcMaxMultiBreakSize > playerData.getMaxMultiBreakSize) {
          playerData.setMaxMultiBreakSize(playerData.calcMaxMultiBreakSize)
          event.getPlayer.sendMessage(
            s"マルチブレイクのサイズ上限が ${ChatColor.GREEN}${playerData.calcMaxMultiBreakSize} ${ChatColor.RESET}になりました。".toSuccessMessage)
          event.getPlayer.playChangeMaxMultiBreakSizeSound()
        }
        totalExp += block.getExp
      }
    }
    if (playerData.getRank == 1000) totalExp = BigDecimal(0.0)
    event.getPlayer.sendActionBar(
      s"Exp: +${totalExp.doubleValue} ${if (playerData.getExpBoost > 1.0)
        s"${ChatColor.YELLOW}× ${playerData.getExpBoost}"
      else ""}")
    Seichi915ServerCore.bossBarMap
      .getOrElse(event.getPlayer, return )
      .setTitle(s"総整地量: ${playerData.getTotalBreakAmount}")
  }
}
