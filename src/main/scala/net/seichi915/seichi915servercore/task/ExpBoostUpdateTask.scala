package net.seichi915.seichi915servercore.task

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.playerdata.PlayerData
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class ExpBoostUpdateTask extends BukkitRunnable {
  override def run(): Unit =
    Seichi915ServerCore.playerDataMap.foreach {
      case (player: Player, playerData: PlayerData) =>
        if (player.hasPotionEffect(PotionEffectType.LUCK))
          player.getPotionEffect(PotionEffectType.LUCK).getAmplifier match {
            case 0 => playerData.setExpBoost(BigDecimal(1.2))
            case 1 => playerData.setExpBoost(BigDecimal(1.5))
            case 2 => playerData.setExpBoost(BigDecimal(2.5))
            case _ => playerData.setExpBoost(BigDecimal(1.0))
          }
    }
}
