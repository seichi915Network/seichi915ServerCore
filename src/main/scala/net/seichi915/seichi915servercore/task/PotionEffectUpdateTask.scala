package net.seichi915.seichi915servercore.task

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.playerdata.PlayerData
import org.bukkit.entity.Player
import org.bukkit.potion.{PotionEffect, PotionEffectType}
import org.bukkit.scheduler.BukkitRunnable

import scala.jdk.CollectionConverters._

class PotionEffectUpdateTask extends BukkitRunnable {
  override def run(): Unit =
    Seichi915ServerCore.playerDataMap.foreach {
      case (player: Player, playerData: PlayerData) =>
        player.getActivePotionEffects.asScala.foreach(potionEffect =>
          player.removePotionEffect(potionEffect.getType))
        if (playerData.isSpeedEffectEnabled)
          player.addPotionEffect(
            new PotionEffect(PotionEffectType.SPEED,
                             310,
                             playerData.getSpeedEffectAmplifier - 1,
                             false,
                             false))
        if (playerData.isHasteEffectEnabled)
          player.addPotionEffect(
            new PotionEffect(PotionEffectType.FAST_DIGGING,
                             310,
                             playerData.getHasteEffectAmplifier - 1,
                             false,
                             false))
        if (playerData.isJumpBoostEffectEnabled)
          player.addPotionEffect(
            new PotionEffect(PotionEffectType.JUMP,
                             310,
                             playerData.getJumpBoostEffectAmplifier - 1,
                             false,
                             false))
        if (playerData.isNightVisionEffectEnabled)
          player.addPotionEffect(
            new PotionEffect(PotionEffectType.NIGHT_VISION,
                             310,
                             0,
                             false,
                             false))
    }
}
