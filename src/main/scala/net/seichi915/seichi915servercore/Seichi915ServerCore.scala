package net.seichi915.seichi915servercore

import net.seichi915.seichi915servercore.listener.InventoryClickListener
import net.seichi915.seichi915servercore.menu.ClickAction
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

import java.util.UUID
import scala.collection.mutable

object Seichi915ServerCore {
  var instance: Seichi915ServerCore = _

  var clickActionMap: mutable.HashMap[UUID, ClickAction] = mutable.HashMap()
}

class Seichi915ServerCore extends JavaPlugin {
  Seichi915ServerCore.instance = this

  override def onEnable(): Unit = {
    Seq(
      new InventoryClickListener
    ).foreach(Bukkit.getPluginManager.registerEvents(_, this))

    getLogger.info("seichi915ServerCoreが有効になりました。")
  }

  override def onDisable(): Unit = {
    getLogger.info("seichi915ServerCoreが無効になりました。")
  }
}
