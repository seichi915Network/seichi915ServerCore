package net.seichi915.seichi915servercore.external

import com.onarandombox.MultiverseCore.MultiverseCore
import net.coreprotect.{CoreProtect, CoreProtectAPI}
import org.bukkit.Bukkit

object ExternalPlugins {
  def getCoreProtectAPI: CoreProtectAPI = CoreProtect.getInstance().getAPI

  def getMultiverseCore: MultiverseCore =
    Bukkit.getPluginManager
      .getPlugin("Multiverse-Core")
      .asInstanceOf[MultiverseCore]
}
