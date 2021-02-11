package net.seichi915.seichi915servercore.listener

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.tooltype.ToolType._
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{ChatColor, GameMode, Material}
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.EquipmentSlot

class PlayerInteractListener extends Listener {
  @EventHandler
  def onPlayerInteract(event: PlayerInteractEvent): Unit = {
    if (event.getPlayer.getGameMode == GameMode.CREATIVE) return
    event.getAction match {
      case Action.LEFT_CLICK_BLOCK =>
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
      case Action.RIGHT_CLICK_AIR | Action.RIGHT_CLICK_BLOCK =>
        if (event.getHand == EquipmentSlot.OFF_HAND) return
        val playerData =
          Seichi915ServerCore.playerDataMap.getOrElse(event.getPlayer, {
            event.getPlayer.kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
            return
          })
        if (event.getPlayer.isSneaking) {
          event.getPlayer.sendMessage(
            s"液体絶対固めるマンを ${if (playerData.isLiquidHardenerEnabled)
              s"${ChatColor.RED}オフ${ChatColor.RESET}"
            else s"${ChatColor.GREEN}オン${ChatColor.RESET}"} にしました。".toNormalMessage)
          event.getPlayer.playToggleMultiBreakSound()
          playerData.setLiquidHardenerEnabled(
            !playerData.isLiquidHardenerEnabled)
        } else {
          event.getPlayer.sendMessage(
            s"マルチブレイクを ${if (playerData.isMultiBreakEnabled)
              s"${ChatColor.RED}オフ${ChatColor.RESET}"
            else s"${ChatColor.GREEN}オン${ChatColor.RESET}"} にしました。".toNormalMessage)
          event.getPlayer.playToggleMultiBreakSound()
          playerData.setMultiBreakEnabled(!playerData.isMultiBreakEnabled)
        }
      case _ =>
    }
  }
}
