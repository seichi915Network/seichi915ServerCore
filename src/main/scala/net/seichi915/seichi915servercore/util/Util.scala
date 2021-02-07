package net.seichi915.seichi915servercore.util

import net.seichi915.seichi915servercore.multibreak.MultiBreak
import net.seichi915.seichi915servercore.util.Implicits._
import org.bukkit.Location
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
    if ((player.getLocation.getBlockX == criterion.getLocation.getBlockX) && (player.getLocation.getBlockZ == criterion.getLocation.getBlockZ))
      if (player.getLocation.getPitch < 0F)
        player.getFacing match {
          case WEST | EAST =>
            firstPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX - (multiBreak.getDepth - 1) / 2,
              criterion.getLocation.getBlockY,
              criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
            )
            secondPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX + (multiBreak.getDepth - 1) / 2,
              criterion.getLocation.getBlockY + multiBreak.getHeight - 1,
              criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
            )
          case SOUTH | NORTH =>
            firstPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
              criterion.getLocation.getBlockY,
              criterion.getLocation.getBlockZ - (multiBreak.getDepth - 1) / 2
            )
            secondPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
              criterion.getLocation.getBlockY + multiBreak.getHeight - 1,
              criterion.getLocation.getBlockZ + (multiBreak.getDepth - 1) / 2
            )
          case _ => return List.empty[Block]
        } else if (player.getLocation.getPitch > 0F)
        player.getFacing match {
          case WEST | EAST =>
            firstPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX - (multiBreak.getDepth - 1) / 2,
              criterion.getLocation.getBlockY - multiBreak.getHeight - 1,
              criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
            )
            secondPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX + (multiBreak.getDepth - 1) / 2,
              criterion.getLocation.getBlockY,
              criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
            )
          case SOUTH | NORTH =>
            firstPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
              criterion.getLocation.getBlockY - multiBreak.getHeight - 1,
              criterion.getLocation.getBlockZ - (multiBreak.getDepth - 1) / 2
            )
            secondPosition = new Location(
              criterion.getLocation.getWorld,
              criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
              criterion.getLocation.getBlockY,
              criterion.getLocation.getBlockZ + (multiBreak.getDepth - 1) / 2
            )
          case _ => return List.empty[Block]
        } else return List.empty[Block]
    else if (criterion.getLocation.getBlockY == player.getLocation.getBlockY)
      player.getFacing match {
        case WEST =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - multiBreak.getDepth - 1,
            criterion.getLocation.getBlockY - (if (player.isSneaking) 1
                                               else 0),
            criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
          )
        case SOUTH =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY - (if (player.isSneaking) 1
                                               else 0),
            criterion.getLocation.getBlockZ
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + multiBreak.getDepth - 1
          )
        case EAST =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX,
            criterion.getLocation.getBlockY - (if (player.isSneaking) 1
                                               else 0),
            criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + multiBreak.getDepth - 1,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
          )
        case NORTH =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY - (if (player.isSneaking) 1
                                               else 0),
            criterion.getLocation.getBlockZ - multiBreak.getDepth - 1
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ
          )
        case _ => return List.empty[Block]
      } else
      player.getFacing match {
        case WEST =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - multiBreak.getDepth - 1,
            criterion.getLocation.getBlockY - 1,
            criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
          )
        case SOUTH =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY - 1,
            criterion.getLocation.getBlockZ
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + multiBreak.getDepth - 1
          )
        case EAST =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX,
            criterion.getLocation.getBlockY - 1,
            criterion.getLocation.getBlockZ - (multiBreak.getWidth - 1) / 2
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + multiBreak.getDepth - 1,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ + (multiBreak.getWidth - 1) / 2
          )
        case NORTH =>
          firstPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX - (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY - 1,
            criterion.getLocation.getBlockZ - multiBreak.getDepth - 1
          )
          secondPosition = new Location(
            criterion.getLocation.getWorld,
            criterion.getLocation.getBlockX + (multiBreak.getWidth - 1) / 2,
            criterion.getLocation.getBlockY + multiBreak.getHeight - 2,
            criterion.getLocation.getBlockZ
          )
        case _ => return List.empty[Block]
      }
    for (y <- 0 until (secondPosition.getBlockY - firstPosition.getBlockY) + 1;
         z <- 0 until (secondPosition.getBlockZ - firstPosition.getBlockZ) + 1;
         x <- 0 until (secondPosition.getBlockX - firstPosition.getBlockX) + 1) {
      val block = criterion.getLocation.getWorld.getBlockAt(
        new Location(criterion.getLocation.getWorld,
                     secondPosition.getBlockX - x,
                     secondPosition.getBlockY - y,
                     secondPosition.getBlockZ - z))
      if (block.canBreak(player))
        targetBlocks = targetBlocks.appended(block)
    }
    targetBlocks
  }
}
