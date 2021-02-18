package net.seichi915.seichi915servercore.command

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.Bukkit
import org.bukkit.command.{
  Command,
  CommandExecutor,
  CommandSender,
  TabCompleter
}
import org.bukkit.util.StringUtil

import java.util
import java.util.Collections
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

class VotePointCommand extends CommandExecutor with TabCompleter {
  override def onCommand(sender: CommandSender,
                         command: Command,
                         label: String,
                         args: Array[String]): Boolean = {
    if (args.length != 3) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    val point = args(2).toIntOption match {
      case Some(value) => value
      case None =>
        sender.sendMessage("投票ポイントは数字で指定してください。".toErrorMessage)
        return true
    }
    args(0).toLowerCase match {
      case "add" =>
        val task = IO {
          val uuidOption = Database.getUUID(args(1))
          if (uuidOption.nonEmpty) {
            val uuid = uuidOption.get
            if (Bukkit.getOfflinePlayer(uuid).isOnline) {
              val playerData =
                Seichi915ServerCore.playerDataMap
                  .getOrElse(Bukkit.getPlayer(uuid), {
                    Bukkit
                      .getPlayer(uuid)
                      .kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
                    return true
                  })
              playerData.setVotePoint(playerData.getVotePoint + point)
            } else {
              val previousPoint = Database.getVotePoint(uuid)
              Database.setVotePoint(uuid, previousPoint + point)
            }
            sender.sendMessage(
              s"${args(1)} さんに投票ポイント $point を付与しました。".toSuccessMessage)
          } else
            sender.sendMessage(s"${args(1)} さんのUUIDを取得できませんでした。".toErrorMessage)
        }
        val contextShift = IO.contextShift(ExecutionContext.global)
        IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
      case "set" =>
        val task = IO {
          val uuidOption = Database.getUUID(args(1))
          if (uuidOption.nonEmpty) {
            val uuid = uuidOption.get
            if (Bukkit.getOfflinePlayer(uuid).isOnline) {
              val playerData =
                Seichi915ServerCore.playerDataMap
                  .getOrElse(Bukkit.getPlayer(uuid), {
                    Bukkit
                      .getPlayer(uuid)
                      .kickPlayer("プレイヤーデータが見つかりませんでした。".toErrorMessage)
                    return true
                  })
              playerData.setVotePoint(point)
            } else
              Database.setVotePoint(uuid, point)
            sender.sendMessage(
              s"${args(1)} さんに投票ポイントを $point に設定しました。".toSuccessMessage)
          } else
            sender.sendMessage(s"${args(1)} さんのUUIDを取得できませんでした。".toErrorMessage)
        }
        val contextShift = IO.contextShift(ExecutionContext.global)
        IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
      case _ =>
        sender.sendMessage("不明なサブコマンドです。".toErrorMessage)
    }
    true
  }

  override def onTabComplete(sender: CommandSender,
                             command: Command,
                             alias: String,
                             args: Array[String]): util.List[String] = {
    val completions = new util.ArrayList[String]()
    args.length match {
      case 1 =>
        StringUtil.copyPartialMatches(args(0),
                                      List("add", "set").asJava,
                                      completions)
      case 2 =>
        StringUtil.copyPartialMatches(args(1),
                                      Database.getNames.asJava,
                                      completions)
      case _ => Collections.emptyList()
    }
    Collections.sort(completions)
    completions
  }
}
