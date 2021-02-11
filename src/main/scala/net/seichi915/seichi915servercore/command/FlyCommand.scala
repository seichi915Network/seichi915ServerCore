package net.seichi915.seichi915servercore.command

import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.command.{
  Command,
  CommandExecutor,
  CommandSender,
  TabCompleter
}
import org.bukkit.entity.Player

import java.util
import java.util.Collections

class FlyCommand extends CommandExecutor with TabCompleter {
  override def onCommand(sender: CommandSender,
                         command: Command,
                         label: String,
                         args: Array[String]): Boolean = {
    if (!sender.isInstanceOf[Player]) {
      sender.sendMessage("このコマンドはプレイヤーのみが実行できます。".toErrorMessage)
      return true
    }
    if (args.nonEmpty) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    val player = sender.asInstanceOf[Player]
    if (player.isFlying) {
      sender.sendMessage("Flyをオフにしました。".toNormalMessage)
      player.setFlying(false)
      player.setAllowFlight(false)
    } else {
      sender.sendMessage("Flyをオンにしました。".toNormalMessage)
      player.setAllowFlight(true)
      player.setFlying(true)
    }
    true
  }

  override def onTabComplete(sender: CommandSender,
                             command: Command,
                             alias: String,
                             args: Array[String]): util.List[String] =
    Collections.emptyList()
}
