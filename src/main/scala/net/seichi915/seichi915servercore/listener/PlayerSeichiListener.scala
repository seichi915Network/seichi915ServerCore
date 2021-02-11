package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.event.PlayerSeichiEvent
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{ChatColor, GameMode}
import org.bukkit.event.{EventHandler, Listener}

class PlayerSeichiListener extends Listener {
  @EventHandler
  def onPlayerSeichi(event: PlayerSeichiEvent): Unit = {
    if (event.getPlayer.getGameMode == GameMode.CREATIVE) return
    val playerData =
      Seichi915ServerCore.playerDataMap.getOrElse(event.getPlayer, {
        event.getPlayer.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
        return
      })
    playerData.setExp(
      playerData.getExp + (event.getBlock.getExp * playerData.getExpBoost))
    while (playerData.canRankUp) {
      event.getPlayer.playRankUpSound()
      event.getPlayer.sendMessage(
        s"ランクアップしました。(${ChatColor.YELLOW}${playerData.getRank} ${ChatColor.RESET} -> ${ChatColor.GREEN}${playerData.getRank + 1}${ChatColor.RESET})".toSuccessMessage)
      playerData.setRank(playerData.getRank + 1)
      playerData.setExp(playerData.getExp - BigDecimal(2000.0))
    }
    event.getPlayer.setLevel(playerData.getRank)
    event.getPlayer.setExp((playerData.getExp / BigDecimal(2000f)).floatValue)
  }
}
