package net.seichi915.seichi915servercore

import org.bukkit.plugin.java.JavaPlugin

object Seichi915ServerCore {
  var instance: Seichi915ServerCore = _
}

class Seichi915ServerCore extends JavaPlugin {
  Seichi915ServerCore.instance = this

  override def onEnable(): Unit = {
    getLogger.info("seichi915ServerCoreが有効になりました。")
  }

  override def onDisable(): Unit = {
    getLogger.info("seichi915ServerCoreが無効になりました。")
  }
}
