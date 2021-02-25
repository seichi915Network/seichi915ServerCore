package net.seichi915.seichi915servercore.util

import cats.effect.IO
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.multibreak.MultiBreak
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.{Location, Material}
import org.bukkit.block.Block
import org.bukkit.block.BlockFace._
import org.bukkit.entity.Player

object Util {
  def calcTargetBlocks(player: Player,
                       criterion: Block,
                       multiBreak: MultiBreak): List[Block] = {
    var targetBlocks = List[Block]()
    var firstPosition = criterion.getLocation.clone()
    var secondPosition = criterion.getLocation.clone()
    if (player.getLocation.getBlockX == criterion.getLocation.getBlockX && player.getLocation.getBlockZ == criterion.getLocation.getBlockZ)
      if (player.getLocation.getPitch < 0F)
        player.getFacing match {
          case NORTH | SOUTH =>
            firstPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
              criterion.getLocation.getBlockY + multiBreak.getHeight - 1,
              criterion.getLocation.getBlockZ + (multiBreak.getDepth - 1) / 2
            )
            secondPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
              criterion.getLocation.getBlockY,
              criterion.getLocation.getBlockZ - (multiBreak.getDepth - 1) / 2
            )
          case WEST | EAST =>
            firstPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX + (multiBreak.getDepth - 1) / 2,
              criterion.getLocation.getBlockY + multiBreak.getHeight - 1,
              criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
            )
            secondPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX - (multiBreak.getDepth - 1) / 2,
              criterion.getLocation.getBlockY,
              criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
            )
          case _ => return List.empty[Block]
        } else
        player.getFacing match {
          case NORTH | SOUTH =>
            firstPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
              criterion.getLocation.getBlockY,
              criterion.getLocation.getBlockZ + (multiBreak.getDepth - 1) / 2,
            )
            secondPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
              criterion.getLocation.getBlockY - (multiBreak.getHeight - 1),
              criterion.getLocation.getBlockZ - (multiBreak.getDepth - 1) / 2
            )
          case WEST | EAST =>
            firstPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX + (multiBreak.getDepth - 1) / 2,
              criterion.getLocation.getBlockY,
              criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
            )
            secondPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX - (multiBreak.getDepth - 1) / 2,
              criterion.getLocation.getBlockY - (multiBreak.getHeight - 1),
              criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
            )
          case _ => return List.empty[Block]
        } else if (player.getLocation.getBlockY == criterion.getLocation.getBlockY && !player.isSneaking)
      player.getFacing match {
        case NORTH =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY,
            criterion.getLocation.getBlockZ - (multiBreak.getDepth - 1)
          )
        case SOUTH =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + multiBreak.getDepth - 1
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY,
            criterion.getLocation.getBlockZ
          )
        case WEST =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + (multiBreak.getDepth - 1) / 2
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getDepth - 1),
            criterion.getLocation.getBlockY,
            criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
          )
        case EAST =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + multiBreak.getDepth - 1,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX,
            criterion.getLocation.getBlockY,
            criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
          )
        case _ => return List.empty[Block]
      } else
      player.getFacing match {
        case NORTH =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY - 1,
            criterion.getLocation.getBlockZ - (multiBreak.getDepth - 1)
          )
        case SOUTH =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + multiBreak.getDepth - 1
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY - 1,
            criterion.getLocation.getBlockZ
          )
        case WEST =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + (multiBreak.getDepth - 1) / 2
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getDepth - 1),
            criterion.getLocation.getBlockY - 1,
            criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
          )
        case EAST =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + multiBreak.getDepth - 1,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX,
            criterion.getLocation.getBlockY - 1,
            criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
          )
        case _ => return List.empty[Block]
      }
    for (y <- 0 until (firstPosition.getBlockY - secondPosition.getBlockY) + 1;
         z <- 0 until (firstPosition.getBlockZ - secondPosition.getBlockZ) + 1;
         x <- 0 until (firstPosition.getBlockX - secondPosition.getBlockX) + 1) {
      val block = criterion.getLocation.getWorld.getBlockAt(
        new Location(criterion.getLocation.getWorld,
                     firstPosition.getBlockX - x,
                     firstPosition.getBlockY - y,
                     firstPosition.getBlockZ - z))
      if (block.canBreak(player))
        targetBlocks = targetBlocks.appended(block)
    }
    targetBlocks
  }

  def fillBlocks(firstPosition: Location,
                 secondPosition: Location,
                 material: Material): Unit =
    for (y <- 0 until (firstPosition.getBlockY - secondPosition.getBlockY) + 1;
         z <- 0 until (firstPosition.getBlockZ - secondPosition.getBlockZ) + 1;
         x <- 0 until (firstPosition.getBlockX - secondPosition.getBlockX) + 1) {
      val block = firstPosition.getWorld.getBlockAt(
        new Location(firstPosition.getWorld,
                     firstPosition.getBlockX - x,
                     firstPosition.getBlockY - y,
                     firstPosition.getBlockZ - z))
      IO(block.setType(material))
        .unsafeRunOnServerThread(Seichi915ServerCore.instance)
    }
}
