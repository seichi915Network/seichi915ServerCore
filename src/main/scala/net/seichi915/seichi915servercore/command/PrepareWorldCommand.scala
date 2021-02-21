package net.seichi915.seichi915servercore.command

import cats.effect.{IO, Timer}
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.util.Implicits._
import net.seichi915.seichi915servercore.util.Util
import org.bukkit.GameRule._
import org.bukkit.{Difficulty, Location, Material}
import org.bukkit.command.{
  Command,
  CommandExecutor,
  CommandSender,
  TabCompleter
}
import org.bukkit.entity.{EntityType, Player}
import org.bukkit.util.StringUtil

import java.util
import java.util.Collections
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._
import scala.language.postfixOps
import scala.util.chaining._

class PrepareWorldCommand extends CommandExecutor with TabCompleter {
  override def onCommand(sender: CommandSender,
                         command: Command,
                         label: String,
                         args: Array[String]): Boolean = {
    if (!sender.isInstanceOf[Player]) {
      sender.sendMessage("このコマンドはプレイヤーのみが実行できます。".toErrorMessage)
      return true
    }
    if (args.length != 1) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    val freeMemory = BigDecimal(Runtime.getRuntime.freeMemory() / 1024) / BigDecimal(
      math
        .pow(1024.0, 2.0))
    if (freeMemory < BigDecimal(13.0)) {
      sender.sendMessage("メモリの空き容量が足りません。(最低13GB以上必要です。)".toErrorMessage)
      return true
    }
    val world = sender.asInstanceOf[Player].getWorld
    world
      .tap(_.setDifficulty(Difficulty.PEACEFUL))
      .tap(_.setGameRule(DO_DAYLIGHT_CYCLE, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(DO_ENTITY_DROPS, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(DO_FIRE_TICK, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(DO_MOB_LOOT, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(DO_MOB_SPAWNING, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(DO_TILE_DROPS, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(DO_WEATHER_CYCLE, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(KEEP_INVENTORY, java.lang.Boolean.TRUE))
      .tap(_.setGameRule(MOB_GRIEFING, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(DO_PATROL_SPAWNING, java.lang.Boolean.FALSE))
      .tap(_.setGameRule(DO_TRADER_SPAWNING, java.lang.Boolean.FALSE))
      .tap {
        _.getEntities.asScala
          .filter(_.getType != EntityType.PLAYER)
          .foreach { entity =>
            entity.teleport(
              new Location(entity.getLocation.getWorld,
                           entity.getLocation.getX,
                           0.0,
                           entity.getLocation.getZ))
            IO(entity.remove())
              .runOnServerThread(Seichi915ServerCore.instance)
          }
      }
    implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
    val task = IO {
      args(0).toLowerCase match {
        case "normal" | "end" =>
          var firstPosition = new Location(world, 6, 64, 6)
          var secondPosition = new Location(world, -6, 64, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 256, 6)
          secondPosition = new Location(world, -6, 65, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 64, -6)
          secondPosition = new Location(world, -6, 64, -1500)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 256, -6)
          secondPosition = new Location(world, -6, 65, -1500)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 64, 1500)
          secondPosition = new Location(world, -6, 64, 6)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 256, 1500)
          secondPosition = new Location(world, -6, 65, 6)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, -6, 64, 6)
          secondPosition = new Location(world, -1500, 64, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, -6, 256, 6)
          secondPosition = new Location(world, -1500, 65, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 1500, 64, 6)
          secondPosition = new Location(world, 6, 64, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 1500, 256, 6)
          secondPosition = new Location(world, 6, 65, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)
        case "nether" =>
          var firstPosition = new Location(world, 6, 64, 6)
          var secondPosition = new Location(world, -6, 64, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 122, 6)
          secondPosition = new Location(world, -6, 65, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 64, -6)
          secondPosition = new Location(world, -6, 64, -1500)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 122, -6)
          secondPosition = new Location(world, -6, 65, -1500)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 64, 1500)
          secondPosition = new Location(world, -6, 64, 6)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 6, 122, 1500)
          secondPosition = new Location(world, -6, 65, 6)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, -6, 64, 6)
          secondPosition = new Location(world, -1500, 64, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, -6, 122, 6)
          secondPosition = new Location(world, -1500, 65, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 1500, 64, 6)
          secondPosition = new Location(world, 6, 64, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.BEDROCK)

          Timer[IO].sleep(10 second)

          firstPosition = new Location(world, 1500, 122, 6)
          secondPosition = new Location(world, 6, 65, -6)
          Util.fillBlocks(firstPosition, secondPosition, Material.AIR)
        case _ =>
          sender.sendMessage("不明なサブコマンドです。".toErrorMessage)
      }
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
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
                                      List("normal", "nether", "end").asJava,
                                      completions)
      case _ => Collections.emptyList()
    }
    Collections.sort(completions)
    completions
  }
}
