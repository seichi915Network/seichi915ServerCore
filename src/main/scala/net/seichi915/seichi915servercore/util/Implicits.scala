package net.seichi915.seichi915servercore.util

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.menu.ClickAction
import net.seichi915.seichi915servercore.playerdata.PlayerData
import org.bukkit.block.Block
import org.bukkit.{ChatColor, Material, NamespacedKey}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

import java.util.UUID
import scala.concurrent.Future

object Implicits {
  implicit class AnyOps(any: Any) {
    def isNull: Boolean = Option(any).flatMap(_ => Some(false)).getOrElse(true)

    def nonNull: Boolean = !isNull
  }

  implicit class ItemStackOps(itemStack: ItemStack) {
    def setClickAction(clickAction: ClickAction): Unit = {
      val itemMeta = itemStack.getItemMeta
      val uuid = UUID.randomUUID()
      itemMeta.getPersistentDataContainer.set(
        new NamespacedKey(Seichi915ServerCore.instance, "click_action"),
        PersistentDataType.STRING,
        uuid.toString)
      itemStack.setItemMeta(itemMeta)
      Seichi915ServerCore.clickActionMap += uuid -> clickAction
    }
  }

  implicit class PlayerOps(player: Player) {
    def getPlayerData: Future[Option[PlayerData]] =
      Database.getPlayerData(player)

    def createNewPlayerData: Future[Unit] = Database.createNewPlayerData(player)
  }

  implicit class StringOps(string: String) {
    def toNormalMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.WHITE}seichi915Server${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toSuccessMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.GREEN}seichi915Server${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toWarningMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.GOLD}seichi915Server${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toErrorMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.RED}seichi915Server${ChatColor.AQUA}]${ChatColor.RESET} $string"
  }

  implicit class BlockOps(block: Block) {
    import Material._
    private val unbreakableMaterials = List(
      AIR,
      COMMAND_BLOCK,
      CHAIN_COMMAND_BLOCK,
      REPEATING_COMMAND_BLOCK,
      BEDROCK,
      STRUCTURE_BLOCK,
      STRUCTURE_VOID,
      BARRIER,
      END_PORTAL_FRAME,
      END_PORTAL,
      NETHER_PORTAL
    )

    def canBreak(player: Player): Boolean = {
      if (block.isNull) return false
      if (unbreakableMaterials.contains(block.getType)) return false
      if (block.getLocation.getBlockY <= 4) return false
      if (player.hasPermission(
            s"worldguard.region.bypass.${block.getWorld.getName}")) return true
      val regionQuery =
        WorldGuard.getInstance.getPlatform.getRegionContainer.createQuery
      val localPlayer = WorldGuardPlugin.inst.wrapPlayer(player)
      val canBuild = regionQuery.testState(
        BukkitAdapter.adapt(block.getLocation),
        localPlayer,
        Flags.BUILD)
      val canBlockBreak = regionQuery.testState(
        BukkitAdapter.adapt(block.getLocation),
        localPlayer,
        Flags.BLOCK_BREAK)
      canBuild && canBlockBreak
    }
  }
}
