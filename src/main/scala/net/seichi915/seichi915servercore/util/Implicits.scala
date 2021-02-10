package net.seichi915.seichi915servercore.util

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.database.Database
import net.seichi915.seichi915servercore.inventory.Seichi915ServerInventoryHolder
import net.seichi915.seichi915servercore.menu.ClickAction
import net.seichi915.seichi915servercore.playerdata.PlayerData
import net.seichi915.seichi915servercore.tooltype.ToolType._
import org.bukkit.block.Block
import org.bukkit.{ChatColor, Material, NamespacedKey, Sound}
import org.bukkit.entity.Player
import org.bukkit.inventory.{Inventory, ItemStack}
import org.bukkit.persistence.PersistentDataType

import java.util.UUID
import scala.concurrent.Future

object Implicits {
  implicit class AnyOps(any: Any) {
    def isNull: Boolean = Option(any).flatMap(_ => Some(false)).getOrElse(true)

    def nonNull: Boolean = !isNull
  }

  implicit class InventoryOps(inventory: Inventory) {
    def isSeichi915ServerInventory: Boolean =
      inventory.getHolder.nonNull && inventory.getHolder
        .isInstanceOf[Seichi915ServerInventoryHolder]
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

    def playMenuButtonClickSound(): Unit =
      player.playSound(player.getLocation, Sound.UI_BUTTON_CLICK, 1, 1)

    def playDisabledMenuButtonClickSound(): Unit =
      player.playSound(player.getLocation,
                       Sound.BLOCK_STONE_BUTTON_CLICK_ON,
                       1,
                       0)

    def playChangeMaxMultiBreakSizeSound(): Unit =
      player.playSound(player.getLocation, Sound.ENTITY_PLAYER_LEVELUP, 1, 1)

    def playLevelUpSound(): Unit =
      player.playSound(player.getLocation, Sound.ENTITY_PLAYER_LEVELUP, 1, 0)
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

    def getUsableToolType: ToolType =
      block.getType match {
        case STONE                              => Pickaxe
        case GRANITE                            => Pickaxe
        case POLISHED_GRANITE                   => Pickaxe
        case DIORITE                            => Pickaxe
        case POLISHED_DIORITE                   => Pickaxe
        case ANDESITE                           => Pickaxe
        case POLISHED_ANDESITE                  => Pickaxe
        case GRASS_BLOCK                        => Shovel
        case DIRT                               => Shovel
        case COARSE_DIRT                        => Shovel
        case PODZOL                             => Shovel
        case CRIMSON_NYLIUM                     => Pickaxe
        case WARPED_NYLIUM                      => Pickaxe
        case COBBLESTONE                        => Pickaxe
        case OAK_PLANKS                         => Axe
        case SPRUCE_PLANKS                      => Axe
        case BIRCH_PLANKS                       => Axe
        case JUNGLE_PLANKS                      => Axe
        case ACACIA_PLANKS                      => Axe
        case DARK_OAK_PLANKS                    => Axe
        case CRIMSON_PLANKS                     => Axe
        case WARPED_PLANKS                      => Axe
        case BEDROCK                            => Other
        case SAND                               => Shovel
        case RED_SAND                           => Shovel
        case GRAVEL                             => Shovel
        case GOLD_ORE                           => Pickaxe
        case IRON_ORE                           => Pickaxe
        case COAL_ORE                           => Pickaxe
        case NETHER_GOLD_ORE                    => Pickaxe
        case OAK_LOG                            => Axe
        case SPRUCE_LOG                         => Axe
        case BIRCH_LOG                          => Axe
        case JUNGLE_LOG                         => Axe
        case ACACIA_LOG                         => Axe
        case DARK_OAK_LOG                       => Axe
        case CRIMSON_STEM                       => Axe
        case WARPED_STEM                        => Axe
        case STRIPPED_OAK_LOG                   => Axe
        case STRIPPED_SPRUCE_LOG                => Axe
        case STRIPPED_BIRCH_LOG                 => Axe
        case STRIPPED_JUNGLE_LOG                => Axe
        case STRIPPED_ACACIA_LOG                => Axe
        case STRIPPED_DARK_OAK_LOG              => Axe
        case STRIPPED_CRIMSON_STEM              => Axe
        case STRIPPED_WARPED_STEM               => Axe
        case STRIPPED_OAK_WOOD                  => Axe
        case STRIPPED_SPRUCE_WOOD               => Axe
        case STRIPPED_BIRCH_WOOD                => Axe
        case STRIPPED_JUNGLE_WOOD               => Axe
        case STRIPPED_ACACIA_WOOD               => Axe
        case STRIPPED_DARK_OAK_WOOD             => Axe
        case STRIPPED_CRIMSON_HYPHAE            => Axe
        case STRIPPED_WARPED_HYPHAE             => Axe
        case OAK_WOOD                           => Axe
        case SPRUCE_WOOD                        => Axe
        case BIRCH_WOOD                         => Axe
        case JUNGLE_WOOD                        => Axe
        case ACACIA_WOOD                        => Axe
        case DARK_OAK_WOOD                      => Axe
        case CRIMSON_HYPHAE                     => Axe
        case WARPED_HYPHAE                      => Axe
        case SPONGE                             => Hoe
        case WET_SPONGE                         => Hoe
        case GLASS                              => AnyTool
        case LAPIS_ORE                          => Pickaxe
        case LAPIS_BLOCK                        => Pickaxe
        case SANDSTONE                          => Pickaxe
        case CHISELED_SANDSTONE                 => Pickaxe
        case CUT_SANDSTONE                      => Pickaxe
        case WHITE_WOOL                         => Shears
        case ORANGE_WOOL                        => Shears
        case MAGENTA_WOOL                       => Shears
        case LIGHT_BLUE_WOOL                    => Shears
        case YELLOW_WOOL                        => Shears
        case LIME_WOOL                          => Shears
        case PINK_WOOL                          => Shears
        case GRAY_WOOL                          => Shears
        case LIGHT_GRAY_WOOL                    => Shears
        case CYAN_WOOL                          => Shears
        case PURPLE_WOOL                        => Shears
        case BLUE_WOOL                          => Shears
        case BROWN_WOOL                         => Shears
        case GREEN_WOOL                         => Shears
        case RED_WOOL                           => Shears
        case BLACK_WOOL                         => Shears
        case GOLD_BLOCK                         => Pickaxe
        case IRON_BLOCK                         => Pickaxe
        case OAK_SLAB                           => Axe
        case SPRUCE_SLAB                        => Axe
        case BIRCH_SLAB                         => Axe
        case JUNGLE_SLAB                        => Axe
        case ACACIA_SLAB                        => Axe
        case DARK_OAK_SLAB                      => Axe
        case CRIMSON_SLAB                       => Axe
        case WARPED_SLAB                        => Axe
        case STONE_SLAB                         => Pickaxe
        case SMOOTH_STONE_SLAB                  => Pickaxe
        case SANDSTONE_SLAB                     => Pickaxe
        case CUT_SANDSTONE_SLAB                 => Pickaxe
        case PETRIFIED_OAK_SLAB                 => Pickaxe
        case COBBLESTONE_SLAB                   => Pickaxe
        case BRICK_SLAB                         => Pickaxe
        case STONE_BRICK_SLAB                   => Pickaxe
        case NETHER_BRICK_SLAB                  => Pickaxe
        case QUARTZ_SLAB                        => Pickaxe
        case RED_SANDSTONE_SLAB                 => Pickaxe
        case CUT_RED_SANDSTONE_SLAB             => Pickaxe
        case PURPUR_SLAB                        => Pickaxe
        case PRISMARINE_SLAB                    => Pickaxe
        case DARK_PRISMARINE_SLAB               => Pickaxe
        case SMOOTH_QUARTZ                      => Pickaxe
        case SMOOTH_RED_SANDSTONE               => Pickaxe
        case SMOOTH_SANDSTONE                   => Pickaxe
        case SMOOTH_STONE                       => Pickaxe
        case BRICKS                             => Pickaxe
        case BOOKSHELF                          => Axe
        case MOSSY_COBBLESTONE                  => Pickaxe
        case OBSIDIAN                           => Pickaxe
        case PURPUR_BLOCK                       => Pickaxe
        case PURPUR_PILLAR                      => Pickaxe
        case PURPUR_STAIRS                      => Pickaxe
        case OAK_STAIRS                         => Axe
        case DIAMOND_ORE                        => Pickaxe
        case DIAMOND_BLOCK                      => Pickaxe
        case COBBLESTONE_STAIRS                 => Pickaxe
        case REDSTONE_ORE                       => Pickaxe
        case ICE                                => Pickaxe
        case SNOW_BLOCK                         => Shovel
        case CLAY                               => Shovel
        case PUMPKIN                            => Axe
        case CARVED_PUMPKIN                     => Axe
        case NETHERRACK                         => Pickaxe
        case SOUL_SAND                          => Shovel
        case SOUL_SOIL                          => Shovel
        case BASALT                             => Pickaxe
        case POLISHED_BASALT                    => Pickaxe
        case GLOWSTONE                          => AnyTool
        case JACK_O_LANTERN                     => Axe
        case STONE_BRICKS                       => Pickaxe
        case MOSSY_STONE_BRICKS                 => Pickaxe
        case CRACKED_STONE_BRICKS               => Pickaxe
        case CHISELED_STONE_BRICKS              => Pickaxe
        case MELON                              => Axe
        case BRICK_STAIRS                       => Pickaxe
        case STONE_BRICK_STAIRS                 => Pickaxe
        case MYCELIUM                           => Shovel
        case NETHER_BRICKS                      => Pickaxe
        case CRACKED_NETHER_BRICKS              => Pickaxe
        case CHISELED_NETHER_BRICKS             => Pickaxe
        case NETHER_BRICK_STAIRS                => Pickaxe
        case END_STONE                          => Pickaxe
        case END_STONE_BRICKS                   => Pickaxe
        case SANDSTONE_STAIRS                   => Pickaxe
        case EMERALD_ORE                        => Pickaxe
        case EMERALD_BLOCK                      => Pickaxe
        case SPRUCE_STAIRS                      => Axe
        case BIRCH_STAIRS                       => Axe
        case JUNGLE_STAIRS                      => Axe
        case CRIMSON_STAIRS                     => Axe
        case WARPED_STAIRS                      => Axe
        case NETHER_QUARTZ_ORE                  => Pickaxe
        case CHISELED_QUARTZ_BLOCK              => Pickaxe
        case QUARTZ_BLOCK                       => Pickaxe
        case QUARTZ_BRICKS                      => Pickaxe
        case QUARTZ_PILLAR                      => Pickaxe
        case QUARTZ_STAIRS                      => Pickaxe
        case WHITE_TERRACOTTA                   => Pickaxe
        case ORANGE_TERRACOTTA                  => Pickaxe
        case MAGENTA_TERRACOTTA                 => Pickaxe
        case LIGHT_BLUE_TERRACOTTA              => Pickaxe
        case YELLOW_TERRACOTTA                  => Pickaxe
        case LIME_TERRACOTTA                    => Pickaxe
        case PINK_TERRACOTTA                    => Pickaxe
        case GRAY_TERRACOTTA                    => Pickaxe
        case LIGHT_GRAY_TERRACOTTA              => Pickaxe
        case CYAN_TERRACOTTA                    => Pickaxe
        case PURPLE_TERRACOTTA                  => Pickaxe
        case BLUE_TERRACOTTA                    => Pickaxe
        case BROWN_TERRACOTTA                   => Pickaxe
        case GREEN_TERRACOTTA                   => Pickaxe
        case RED_TERRACOTTA                     => Pickaxe
        case BLACK_TERRACOTTA                   => Pickaxe
        case HAY_BLOCK                          => Hoe
        case TERRACOTTA                         => Pickaxe
        case COAL_BLOCK                         => Pickaxe
        case PACKED_ICE                         => Pickaxe
        case ACACIA_STAIRS                      => Axe
        case DARK_OAK_STAIRS                    => Axe
        case WHITE_STAINED_GLASS                => AnyTool
        case ORANGE_STAINED_GLASS               => AnyTool
        case MAGENTA_STAINED_GLASS              => AnyTool
        case LIGHT_BLUE_STAINED_GLASS           => AnyTool
        case YELLOW_STAINED_GLASS               => AnyTool
        case LIME_STAINED_GLASS                 => AnyTool
        case PINK_STAINED_GLASS                 => AnyTool
        case GRAY_STAINED_GLASS                 => AnyTool
        case LIGHT_GRAY_STAINED_GLASS           => AnyTool
        case CYAN_STAINED_GLASS                 => AnyTool
        case PURPLE_STAINED_GLASS               => AnyTool
        case BLUE_STAINED_GLASS                 => AnyTool
        case BROWN_STAINED_GLASS                => AnyTool
        case GREEN_STAINED_GLASS                => AnyTool
        case RED_STAINED_GLASS                  => AnyTool
        case BLACK_STAINED_GLASS                => AnyTool
        case PRISMARINE                         => Pickaxe
        case PRISMARINE_BRICKS                  => Pickaxe
        case DARK_PRISMARINE                    => Pickaxe
        case PRISMARINE_STAIRS                  => Pickaxe
        case PRISMARINE_BRICK_STAIRS            => Pickaxe
        case DARK_PRISMARINE_STAIRS             => Pickaxe
        case SEA_LANTERN                        => AnyTool
        case RED_SANDSTONE                      => Pickaxe
        case CHISELED_RED_SANDSTONE             => Pickaxe
        case CUT_RED_SANDSTONE                  => Pickaxe
        case RED_SANDSTONE_STAIRS               => Pickaxe
        case MAGMA_BLOCK                        => Pickaxe
        case NETHER_WART_BLOCK                  => Hoe
        case WARPED_WART_BLOCK                  => Hoe
        case RED_NETHER_BRICKS                  => Pickaxe
        case BONE_BLOCK                         => Pickaxe
        case WHITE_CONCRETE                     => Pickaxe
        case ORANGE_CONCRETE                    => Pickaxe
        case MAGENTA_CONCRETE                   => Pickaxe
        case LIGHT_BLUE_CONCRETE                => Pickaxe
        case YELLOW_CONCRETE                    => Pickaxe
        case LIME_CONCRETE                      => Pickaxe
        case PINK_CONCRETE                      => Pickaxe
        case GRAY_CONCRETE                      => Pickaxe
        case LIGHT_GRAY_CONCRETE                => Pickaxe
        case CYAN_CONCRETE                      => Pickaxe
        case PURPLE_CONCRETE                    => Pickaxe
        case BLUE_CONCRETE                      => Pickaxe
        case BROWN_CONCRETE                     => Pickaxe
        case GREEN_CONCRETE                     => Pickaxe
        case RED_CONCRETE                       => Pickaxe
        case BLACK_CONCRETE                     => Pickaxe
        case WHITE_CONCRETE_POWDER              => Shovel
        case ORANGE_CONCRETE_POWDER             => Shovel
        case MAGENTA_CONCRETE_POWDER            => Shovel
        case LIGHT_BLUE_CONCRETE_POWDER         => Shovel
        case YELLOW_CONCRETE_POWDER             => Shovel
        case LIME_CONCRETE_POWDER               => Shovel
        case PINK_CONCRETE_POWDER               => Shovel
        case GRAY_CONCRETE_POWDER               => Shovel
        case LIGHT_GRAY_CONCRETE_POWDER         => Shovel
        case CYAN_CONCRETE_POWDER               => Shovel
        case PURPLE_CONCRETE_POWDER             => Shovel
        case BLUE_CONCRETE_POWDER               => Shovel
        case BROWN_CONCRETE_POWDER              => Shovel
        case GREEN_CONCRETE_POWDER              => Shovel
        case RED_CONCRETE_POWDER                => Shovel
        case BLACK_CONCRETE_POWDER              => Shovel
        case DEAD_TUBE_CORAL_BLOCK              => Pickaxe
        case DEAD_BRAIN_CORAL_BLOCK             => Pickaxe
        case DEAD_BUBBLE_CORAL_BLOCK            => Pickaxe
        case DEAD_FIRE_CORAL_BLOCK              => Pickaxe
        case DEAD_HORN_CORAL_BLOCK              => Pickaxe
        case TUBE_CORAL_BLOCK                   => Pickaxe
        case BRAIN_CORAL_BLOCK                  => Pickaxe
        case BUBBLE_CORAL_BLOCK                 => Pickaxe
        case FIRE_CORAL_BLOCK                   => Pickaxe
        case HORN_CORAL_BLOCK                   => Pickaxe
        case BLUE_ICE                           => Pickaxe
        case POLISHED_GRANITE_STAIRS            => Pickaxe
        case SMOOTH_RED_SANDSTONE_STAIRS        => Pickaxe
        case MOSSY_STONE_BRICK_STAIRS           => Pickaxe
        case POLISHED_DIORITE_STAIRS            => Pickaxe
        case MOSSY_COBBLESTONE_STAIRS           => Pickaxe
        case END_STONE_BRICK_STAIRS             => Pickaxe
        case STONE_STAIRS                       => Pickaxe
        case SMOOTH_SANDSTONE_STAIRS            => Pickaxe
        case SMOOTH_QUARTZ_STAIRS               => Pickaxe
        case GRANITE_STAIRS                     => Pickaxe
        case ANDESITE_STAIRS                    => Pickaxe
        case RED_NETHER_BRICK_STAIRS            => Pickaxe
        case POLISHED_ANDESITE_STAIRS           => Pickaxe
        case DIORITE_STAIRS                     => Pickaxe
        case POLISHED_GRANITE_SLAB              => Pickaxe
        case SMOOTH_RED_SANDSTONE_SLAB          => Pickaxe
        case MOSSY_STONE_BRICK_SLAB             => Pickaxe
        case POLISHED_DIORITE_SLAB              => Pickaxe
        case MOSSY_COBBLESTONE_SLAB             => Pickaxe
        case END_STONE_BRICK_SLAB               => Pickaxe
        case SMOOTH_SANDSTONE_SLAB              => Pickaxe
        case SMOOTH_QUARTZ_SLAB                 => Pickaxe
        case GRANITE_SLAB                       => Pickaxe
        case ANDESITE_SLAB                      => Pickaxe
        case RED_NETHER_BRICK_SLAB              => Pickaxe
        case POLISHED_ANDESITE_SLAB             => Pickaxe
        case DIORITE_SLAB                       => Pickaxe
        case DRIED_KELP_BLOCK                   => Hoe
        case NETHERITE_BLOCK                    => Pickaxe
        case ANCIENT_DEBRIS                     => Pickaxe
        case CRYING_OBSIDIAN                    => Pickaxe
        case BLACKSTONE                         => Pickaxe
        case BLACKSTONE_SLAB                    => Pickaxe
        case BLACKSTONE_STAIRS                  => Pickaxe
        case GILDED_BLACKSTONE                  => Pickaxe
        case POLISHED_BLACKSTONE                => Pickaxe
        case POLISHED_BLACKSTONE_SLAB           => Pickaxe
        case POLISHED_BLACKSTONE_STAIRS         => Pickaxe
        case CHISELED_POLISHED_BLACKSTONE       => Pickaxe
        case POLISHED_BLACKSTONE_BRICKS         => Pickaxe
        case POLISHED_BLACKSTONE_BRICK_SLAB     => Pickaxe
        case POLISHED_BLACKSTONE_BRICK_STAIRS   => Pickaxe
        case CRACKED_POLISHED_BLACKSTONE_BRICKS => Pickaxe
        case OAK_SAPLING                        => AnyTool
        case SPRUCE_SAPLING                     => AnyTool
        case BIRCH_SAPLING                      => AnyTool
        case JUNGLE_SAPLING                     => AnyTool
        case ACACIA_SAPLING                     => AnyTool
        case DARK_OAK_SAPLING                   => AnyTool
        case OAK_LEAVES                         => Shears
        case SPRUCE_LEAVES                      => Shears
        case BIRCH_LEAVES                       => Shears
        case JUNGLE_LEAVES                      => Shears
        case ACACIA_LEAVES                      => Shears
        case DARK_OAK_LEAVES                    => Shears
        case COBWEB                             => Shears
        case GRASS                              => Shears
        case FERN                               => Shears
        case DEAD_BUSH                          => Shears
        case SEAGRASS                           => Shears
        case SEA_PICKLE                         => AnyTool
        case DANDELION                          => AnyTool
        case POPPY                              => AnyTool
        case BLUE_ORCHID                        => AnyTool
        case ALLIUM                             => AnyTool
        case AZURE_BLUET                        => AnyTool
        case RED_TULIP                          => AnyTool
        case ORANGE_TULIP                       => AnyTool
        case WHITE_TULIP                        => AnyTool
        case PINK_TULIP                         => AnyTool
        case OXEYE_DAISY                        => AnyTool
        case CORNFLOWER                         => AnyTool
        case LILY_OF_THE_VALLEY                 => AnyTool
        case WITHER_ROSE                        => AnyTool
        case BROWN_MUSHROOM                     => AnyTool
        case RED_MUSHROOM                       => AnyTool
        case CRIMSON_FUNGUS                     => AnyTool
        case WARPED_FUNGUS                      => AnyTool
        case CRIMSON_ROOTS                      => AnyTool
        case WARPED_ROOTS                       => AnyTool
        case NETHER_SPROUTS                     => Shears
        case WEEPING_VINES                      => AnyTool
        case TWISTING_VINES                     => AnyTool
        case SUGAR_CANE                         => AnyTool
        case KELP                               => AnyTool
        case BAMBOO                             => Axe
        case TORCH                              => AnyTool
        case END_ROD                            => AnyTool
        case CHORUS_PLANT                       => Axe
        case CHORUS_FLOWER                      => Axe
        case CHEST                              => Axe
        case CRAFTING_TABLE                     => Axe
        case FARMLAND                           => Shovel
        case FURNACE                            => Pickaxe
        case LADDER                             => Axe
        case SNOW                               => Shovel
        case CACTUS                             => AnyTool
        case JUKEBOX                            => Axe
        case OAK_FENCE                          => Axe
        case SPRUCE_FENCE                       => Axe
        case BIRCH_FENCE                        => Axe
        case JUNGLE_FENCE                       => Axe
        case ACACIA_FENCE                       => Axe
        case DARK_OAK_FENCE                     => Axe
        case CRIMSON_FENCE                      => Axe
        case WARPED_FENCE                       => Axe
        case SOUL_TORCH                         => AnyTool
        case INFESTED_STONE                     => Sword
        case INFESTED_COBBLESTONE               => Sword
        case INFESTED_STONE_BRICKS              => Sword
        case INFESTED_MOSSY_STONE_BRICKS        => Sword
        case INFESTED_CRACKED_STONE_BRICKS      => Sword
        case INFESTED_CHISELED_STONE_BRICKS     => Sword
        case BROWN_MUSHROOM_BLOCK               => Axe
        case RED_MUSHROOM_BLOCK                 => Axe
        case MUSHROOM_STEM                      => Axe
        case IRON_BARS                          => Pickaxe
        case CHAIN                              => Pickaxe
        case GLASS_PANE                         => AnyTool
        case VINE                               => Shears
        case LILY_PAD                           => AnyTool
        case NETHER_BRICK_FENCE                 => Pickaxe
        case ENCHANTING_TABLE                   => Pickaxe
        case END_PORTAL_FRAME                   => Other
        case ENDER_CHEST                        => Pickaxe
        case COBBLESTONE_WALL                   => Pickaxe
        case MOSSY_COBBLESTONE_WALL             => Pickaxe
        case BRICK_WALL                         => Pickaxe
        case PRISMARINE_WALL                    => Pickaxe
        case RED_SANDSTONE_WALL                 => Pickaxe
        case MOSSY_STONE_BRICK_WALL             => Pickaxe
        case GRANITE_WALL                       => Pickaxe
        case STONE_BRICK_WALL                   => Pickaxe
        case NETHER_BRICK_WALL                  => Pickaxe
        case ANDESITE_WALL                      => Pickaxe
        case RED_NETHER_BRICK_WALL              => Pickaxe
        case SANDSTONE_WALL                     => Pickaxe
        case END_STONE_BRICK_WALL               => Pickaxe
        case DIORITE_WALL                       => Pickaxe
        case BLACKSTONE_WALL                    => Pickaxe
        case POLISHED_BLACKSTONE_WALL           => Pickaxe
        case POLISHED_BLACKSTONE_BRICK_WALL     => Pickaxe
        case ANVIL                              => Pickaxe
        case CHIPPED_ANVIL                      => Pickaxe
        case DAMAGED_ANVIL                      => Pickaxe
        case WHITE_CARPET                       => AnyTool
        case ORANGE_CARPET                      => AnyTool
        case MAGENTA_CARPET                     => AnyTool
        case LIGHT_BLUE_CARPET                  => AnyTool
        case YELLOW_CARPET                      => AnyTool
        case LIME_CARPET                        => AnyTool
        case PINK_CARPET                        => AnyTool
        case GRAY_CARPET                        => AnyTool
        case LIGHT_GRAY_CARPET                  => AnyTool
        case CYAN_CARPET                        => AnyTool
        case PURPLE_CARPET                      => AnyTool
        case BLUE_CARPET                        => AnyTool
        case BROWN_CARPET                       => AnyTool
        case GREEN_CARPET                       => AnyTool
        case RED_CARPET                         => AnyTool
        case BLACK_CARPET                       => AnyTool
        case SLIME_BLOCK                        => AnyTool
        case GRASS_PATH                         => Shovel
        case SUNFLOWER                          => AnyTool
        case LILAC                              => AnyTool
        case ROSE_BUSH                          => AnyTool
        case PEONY                              => AnyTool
        case TALL_GRASS                         => Shears
        case LARGE_FERN                         => Shears
        case WHITE_STAINED_GLASS_PANE           => AnyTool
        case ORANGE_STAINED_GLASS_PANE          => AnyTool
        case MAGENTA_STAINED_GLASS_PANE         => AnyTool
        case LIGHT_BLUE_STAINED_GLASS_PANE      => AnyTool
        case YELLOW_STAINED_GLASS_PANE          => AnyTool
        case LIME_STAINED_GLASS_PANE            => AnyTool
        case PINK_STAINED_GLASS_PANE            => AnyTool
        case GRAY_STAINED_GLASS_PANE            => AnyTool
        case LIGHT_GRAY_STAINED_GLASS_PANE      => AnyTool
        case CYAN_STAINED_GLASS_PANE            => AnyTool
        case PURPLE_STAINED_GLASS_PANE          => AnyTool
        case BLUE_STAINED_GLASS_PANE            => AnyTool
        case BROWN_STAINED_GLASS_PANE           => AnyTool
        case GREEN_STAINED_GLASS_PANE           => AnyTool
        case RED_STAINED_GLASS_PANE             => AnyTool
        case BLACK_STAINED_GLASS_PANE           => AnyTool
        case SHULKER_BOX                        => Pickaxe
        case WHITE_SHULKER_BOX                  => Pickaxe
        case ORANGE_SHULKER_BOX                 => Pickaxe
        case MAGENTA_SHULKER_BOX                => Pickaxe
        case LIGHT_BLUE_SHULKER_BOX             => Pickaxe
        case YELLOW_SHULKER_BOX                 => Pickaxe
        case LIME_SHULKER_BOX                   => Pickaxe
        case PINK_SHULKER_BOX                   => Pickaxe
        case GRAY_SHULKER_BOX                   => Pickaxe
        case LIGHT_GRAY_SHULKER_BOX             => Pickaxe
        case CYAN_SHULKER_BOX                   => Pickaxe
        case PURPLE_SHULKER_BOX                 => Pickaxe
        case BLUE_SHULKER_BOX                   => Pickaxe
        case BROWN_SHULKER_BOX                  => Pickaxe
        case GREEN_SHULKER_BOX                  => Pickaxe
        case RED_SHULKER_BOX                    => Pickaxe
        case BLACK_SHULKER_BOX                  => Pickaxe
        case WHITE_GLAZED_TERRACOTTA            => Pickaxe
        case ORANGE_GLAZED_TERRACOTTA           => Pickaxe
        case MAGENTA_GLAZED_TERRACOTTA          => Pickaxe
        case LIGHT_BLUE_GLAZED_TERRACOTTA       => Pickaxe
        case YELLOW_GLAZED_TERRACOTTA           => Pickaxe
        case LIME_GLAZED_TERRACOTTA             => Pickaxe
        case PINK_GLAZED_TERRACOTTA             => Pickaxe
        case GRAY_GLAZED_TERRACOTTA             => Pickaxe
        case LIGHT_GRAY_GLAZED_TERRACOTTA       => Pickaxe
        case CYAN_GLAZED_TERRACOTTA             => Pickaxe
        case PURPLE_GLAZED_TERRACOTTA           => Pickaxe
        case BLUE_GLAZED_TERRACOTTA             => Pickaxe
        case BROWN_GLAZED_TERRACOTTA            => Pickaxe
        case GREEN_GLAZED_TERRACOTTA            => Pickaxe
        case RED_GLAZED_TERRACOTTA              => Pickaxe
        case BLACK_GLAZED_TERRACOTTA            => Pickaxe
        case TUBE_CORAL                         => AnyTool
        case BRAIN_CORAL                        => AnyTool
        case BUBBLE_CORAL                       => AnyTool
        case FIRE_CORAL                         => AnyTool
        case HORN_CORAL                         => AnyTool
        case DEAD_BRAIN_CORAL                   => AnyTool
        case DEAD_BUBBLE_CORAL                  => AnyTool
        case DEAD_FIRE_CORAL                    => AnyTool
        case DEAD_HORN_CORAL                    => AnyTool
        case DEAD_TUBE_CORAL                    => AnyTool
        case TUBE_CORAL_FAN                     => AnyTool
        case BRAIN_CORAL_FAN                    => AnyTool
        case BUBBLE_CORAL_FAN                   => AnyTool
        case FIRE_CORAL_FAN                     => AnyTool
        case HORN_CORAL_FAN                     => AnyTool
        case DEAD_TUBE_CORAL_FAN                => AnyTool
        case DEAD_BRAIN_CORAL_FAN               => AnyTool
        case DEAD_BUBBLE_CORAL_FAN              => AnyTool
        case DEAD_FIRE_CORAL_FAN                => AnyTool
        case DEAD_HORN_CORAL_FAN                => AnyTool
        case SCAFFOLDING                        => AnyTool
        case OAK_SIGN                           => Axe
        case SPRUCE_SIGN                        => Axe
        case BIRCH_SIGN                         => Axe
        case JUNGLE_SIGN                        => Axe
        case ACACIA_SIGN                        => Axe
        case DARK_OAK_SIGN                      => Axe
        case CRIMSON_SIGN                       => Axe
        case WARPED_SIGN                        => Axe
        case WHITE_BED                          => AnyTool
        case ORANGE_BED                         => AnyTool
        case MAGENTA_BED                        => AnyTool
        case LIGHT_BLUE_BED                     => AnyTool
        case YELLOW_BED                         => AnyTool
        case LIME_BED                           => AnyTool
        case PINK_BED                           => AnyTool
        case GRAY_BED                           => AnyTool
        case LIGHT_GRAY_BED                     => AnyTool
        case CYAN_BED                           => AnyTool
        case PURPLE_BED                         => AnyTool
        case BLUE_BED                           => AnyTool
        case BROWN_BED                          => AnyTool
        case GREEN_BED                          => AnyTool
        case RED_BED                            => AnyTool
        case BLACK_BED                          => AnyTool
        case FLOWER_POT                         => AnyTool
        case SKELETON_SKULL                     => AnyTool
        case WITHER_SKELETON_SKULL              => AnyTool
        case PLAYER_HEAD                        => AnyTool
        case ZOMBIE_HEAD                        => AnyTool
        case CREEPER_HEAD                       => AnyTool
        case DRAGON_HEAD                        => AnyTool
        case WHITE_BANNER                       => Axe
        case ORANGE_BANNER                      => Axe
        case MAGENTA_BANNER                     => Axe
        case LIGHT_BLUE_BANNER                  => Axe
        case YELLOW_BANNER                      => Axe
        case LIME_BANNER                        => Axe
        case PINK_BANNER                        => Axe
        case GRAY_BANNER                        => Axe
        case LIGHT_GRAY_BANNER                  => Axe
        case CYAN_BANNER                        => Axe
        case PURPLE_BANNER                      => Axe
        case BLUE_BANNER                        => Axe
        case BROWN_BANNER                       => Axe
        case GREEN_BANNER                       => Axe
        case RED_BANNER                         => Axe
        case BLACK_BANNER                       => Axe
        case LOOM                               => Axe
        case COMPOSTER                          => Axe
        case BARREL                             => Axe
        case SMOKER                             => Pickaxe
        case BLAST_FURNACE                      => Pickaxe
        case CARTOGRAPHY_TABLE                  => Axe
        case FLETCHING_TABLE                    => Axe
        case GRINDSTONE                         => Pickaxe
        case SMITHING_TABLE                     => Pickaxe
        case STONECUTTER                        => Pickaxe
        case BELL                               => Pickaxe
        case LANTERN                            => Pickaxe
        case SOUL_LANTERN                       => Pickaxe
        case CAMPFIRE                           => Axe
        case SOUL_CAMPFIRE                      => Axe
        case SHROOMLIGHT                        => Hoe
        case BEE_NEST                           => Axe
        case BEEHIVE                            => Axe
        case HONEYCOMB_BLOCK                    => AnyTool
        case LODESTONE                          => Pickaxe
        case RESPAWN_ANCHOR                     => Pickaxe
        case DISPENSER                          => Pickaxe
        case NOTE_BLOCK                         => Axe
        case STICKY_PISTON                      => Pickaxe
        case PISTON                             => Axe
        case TNT                                => AnyTool
        case LEVER                              => AnyTool
        case STONE_PRESSURE_PLATE               => Pickaxe
        case OAK_PRESSURE_PLATE                 => Axe
        case SPRUCE_PRESSURE_PLATE              => Axe
        case BIRCH_PRESSURE_PLATE               => Axe
        case JUNGLE_PRESSURE_PLATE              => Axe
        case ACACIA_PRESSURE_PLATE              => Axe
        case DARK_OAK_PRESSURE_PLATE            => Axe
        case CRIMSON_PRESSURE_PLATE             => Axe
        case WARPED_PRESSURE_PLATE              => Axe
        case POLISHED_BLACKSTONE_PRESSURE_PLATE => Pickaxe
        case REDSTONE_TORCH                     => AnyTool
        case OAK_TRAPDOOR                       => Axe
        case SPRUCE_TRAPDOOR                    => Axe
        case BIRCH_TRAPDOOR                     => Axe
        case JUNGLE_TRAPDOOR                    => Axe
        case ACACIA_TRAPDOOR                    => Axe
        case DARK_OAK_TRAPDOOR                  => Axe
        case CRIMSON_TRAPDOOR                   => Axe
        case WARPED_TRAPDOOR                    => Axe
        case OAK_FENCE_GATE                     => Axe
        case SPRUCE_FENCE_GATE                  => Axe
        case BIRCH_FENCE_GATE                   => Axe
        case JUNGLE_FENCE_GATE                  => Axe
        case ACACIA_FENCE_GATE                  => Axe
        case DARK_OAK_FENCE_GATE                => Axe
        case CRIMSON_FENCE_GATE                 => Axe
        case WARPED_FENCE_GATE                  => Axe
        case REDSTONE_LAMP                      => AnyTool
        case TRIPWIRE_HOOK                      => AnyTool
        case STONE_BUTTON                       => Pickaxe
        case OAK_BUTTON                         => Axe
        case SPRUCE_BUTTON                      => Axe
        case BIRCH_BUTTON                       => Axe
        case JUNGLE_BUTTON                      => Axe
        case ACACIA_BUTTON                      => Axe
        case DARK_OAK_BUTTON                    => Axe
        case CRIMSON_BUTTON                     => Axe
        case WARPED_BUTTON                      => Axe
        case POLISHED_BLACKSTONE_BUTTON         => Pickaxe
        case TRAPPED_CHEST                      => Axe
        case LIGHT_WEIGHTED_PRESSURE_PLATE      => Pickaxe
        case HEAVY_WEIGHTED_PRESSURE_PLATE      => Pickaxe
        case DAYLIGHT_DETECTOR                  => Axe
        case REDSTONE_BLOCK                     => Pickaxe
        case HOPPER                             => Pickaxe
        case DROPPER                            => Pickaxe
        case IRON_TRAPDOOR                      => Pickaxe
        case OBSERVER                           => Pickaxe
        case IRON_DOOR                          => Pickaxe
        case OAK_DOOR                           => Axe
        case SPRUCE_DOOR                        => Axe
        case BIRCH_DOOR                         => Axe
        case JUNGLE_DOOR                        => Axe
        case ACACIA_DOOR                        => Axe
        case DARK_OAK_DOOR                      => Axe
        case CRIMSON_DOOR                       => Axe
        case WARPED_DOOR                        => Axe
        case REPEATER                           => AnyTool
        case COMPARATOR                         => AnyTool
        case REDSTONE_WIRE                      => AnyTool
        case LECTERN                            => Axe
        case TARGET                             => Hoe
        case POWERED_RAIL                       => Pickaxe
        case DETECTOR_RAIL                      => Pickaxe
        case RAIL                               => Pickaxe
        case ACTIVATOR_RAIL                     => Pickaxe
        case BREWING_STAND                      => Pickaxe
        case CAULDRON                           => Pickaxe
        case CAKE                               => AnyTool
        case BEACON                             => Pickaxe
        case TURTLE_EGG                         => AnyTool
        case CONDUIT                            => Pickaxe
        case _                                  => Other
      }
  }
}
