package net.seichi915.seichi915servercore.database

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.multibreak.MultiBreak
import net.seichi915.seichi915servercore.playerdata.PlayerData
import org.bukkit.{Bukkit, OfflinePlayer}
import org.bukkit.entity.Player
import scalikejdbc._

import java.io.{File, FileOutputStream}
import java.util.UUID

object Database {
  Class.forName("org.sqlite.JDBC")

  ConnectionPool.singleton(
    s"jdbc:sqlite:${Seichi915ServerCore.instance.getDataFolder.getAbsolutePath}/database.db",
    "",
    "")

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = false
  )

  def saveDefaultDatabase: Boolean =
    try {
      if (!Seichi915ServerCore.instance.getDataFolder.exists())
        Seichi915ServerCore.instance.getDataFolder.mkdir()
      val databaseFile =
        new File(Seichi915ServerCore.instance.getDataFolder, "database.db")
      if (!databaseFile.exists()) {
        val inputStream =
          Seichi915ServerCore.instance.getResource("database.db")
        val outputStream = new FileOutputStream(databaseFile)
        val bytes = new Array[Byte](1024)
        var read = 0
        while ({
          read = inputStream.read(bytes)
          read
        } != -1) outputStream.write(bytes, 0, read)
        inputStream.close()
        outputStream.close()
      }
      true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        false
    }

  def getPlayerData(player: Player): Option[PlayerData] =
    DB localTx { implicit session =>
      sql"SELECT * FROM playerdata WHERE uuid = ${player.getUniqueId}"
        .map(resultSet =>
          PlayerData(
            resultSet.long("total_break_amount"),
            resultSet.int("rank"),
            BigDecimal(resultSet.double("exp")),
            BigDecimal(1.0),
            resultSet.int("vote_point"),
            resultSet.int("max_multibreak_size"),
            resultSet.boolean("multibreak_enabled"),
            MultiBreak(resultSet.int("multibreak_width"),
                       resultSet.int("multibreak_height"),
                       resultSet.int("multibreak_depth")),
            resultSet.boolean("liquid_hardener_enabled"),
            MultiBreak(resultSet.int("liquid_hardener_width"),
                       resultSet.int("liquid_hardener_height"),
                       resultSet.int("liquid_hardener_depth")),
            resultSet.boolean("speed_effect_enabled"),
            resultSet.int("speed_effect_amplifier"),
            resultSet.boolean("haste_effect_enabled"),
            resultSet.int("haste_effect_amplifier"),
            resultSet.boolean("jump_boost_effect_enabled"),
            resultSet.int("jump_boost_effect_amplifier"),
            resultSet.boolean("night_vision_effect_enabled")
        ))
        .list()
        .apply()
        .headOption
    }

  def getPlayerAndBreakAmount: List[(OfflinePlayer, Long)] =
    DB localTx { implicit session =>
      sql"SELECT * FROM playerdata"
        .map(resultSet =>
          (Bukkit.getOfflinePlayer(UUID.fromString(resultSet.string("uuid"))),
           resultSet.long("total_break_amount")))
        .list()
        .apply()
    }

  def createNewPlayerData(player: Player): Unit =
    DB localTx { implicit session =>
      sql"""INSERT INTO playerdata (
           uuid,
           name,
           total_break_amount,
           rank,
           exp,
           vote_point,
           max_multibreak_size,
           multibreak_enabled,
           multibreak_width,
           multibreak_height,
           multibreak_depth,
           liquid_hardener_enabled,
           liquid_hardener_width,
           liquid_hardener_height,
           liquid_hardener_depth,
           speed_effect_enabled,
           speed_effect_amplifier,
           haste_effect_enabled,
           haste_effect_amplifier,
           jump_boost_effect_enabled,
           jump_boost_effect_amplifier,
           night_vision_effect_enabled
           ) VALUES (
           ${player.getUniqueId},
           ${player.getName},
           0,
           1,
           0,
           0,
           3,
           true,
           3,
           3,
           3,
           true,
           3,
           3,
           3,
           true,
           10,
           true,
           2,
           false,
           1,
           true
           )"""
        .update()
        .apply()
    }

  def savePlayerData(player: Player, playerData: PlayerData): Unit =
    DB localTx { implicit session =>
      sql"""UPDATE playerdata SET
             name=${player.getName},
             total_break_amount=${playerData.getTotalBreakAmount},
             rank=${playerData.getRank},
             exp=${playerData.getExp.doubleValue},
             vote_point=${playerData.getVotePoint},
             max_multibreak_size=${playerData.getMaxMultiBreakSize},
             multibreak_enabled=${playerData.isMultiBreakEnabled},
             multibreak_width=${playerData.getMultiBreak.getWidth},
             multibreak_height=${playerData.getMultiBreak.getHeight},
             multibreak_depth=${playerData.getMultiBreak.getDepth},
             liquid_hardener_enabled=${playerData.isLiquidHardenerEnabled},
             liquid_hardener_width=${playerData.getLiquidHardener.getWidth},
             liquid_hardener_height=${playerData.getLiquidHardener.getHeight},
             liquid_hardener_depth=${playerData.getLiquidHardener.getDepth},
             speed_effect_enabled=${playerData.isSpeedEffectEnabled},
             speed_effect_amplifier=${playerData.getSpeedEffectAmplifier},
             haste_effect_enabled=${playerData.isHasteEffectEnabled},
             haste_effect_amplifier=${playerData.getHasteEffectAmplifier},
             jump_boost_effect_enabled=${playerData.isJumpBoostEffectEnabled},
             jump_boost_effect_amplifier=${playerData.getJumpBoostEffectAmplifier},
             night_vision_effect_enabled=${playerData.isNightVisionEffectEnabled}
             WHERE uuid = ${player.getUniqueId}"""
        .update()
        .apply()
    }

  def updatePlayerNameIfChanged(player: Player): Unit = {
    val previousPlayerName = DB localTx { implicit session =>
      sql"SELECT name FROM playerdata WHERE uuid = ${player.getUniqueId}"
        .map(_.string("name"))
        .list()
        .apply()
        .head
    }
    if (!player.getName.equals(previousPlayerName))
      DB localTx { implicit session =>
        sql"UPDATE playerdata SET name=${player.getName} WHERE uuid = ${player.getUniqueId}"
          .update()
          .apply()
      }
  }

  def getName(uuid: UUID): Option[String] =
    DB localTx { implicit session =>
      sql"SELECT name FROM playerdata WHERE uuid = $uuid"
        .map(_.string("name"))
        .list()
        .apply()
        .headOption
    }

  def getNames: List[String] =
    DB localTx { implicit session =>
      sql"SELECT name FROM playerdata"
        .map(_.string("name"))
        .list()
        .apply()
    }

  def getUUID(name: String): Option[UUID] =
    DB localTx { implicit session =>
      sql"SELECT uuid FROM playerdata WHERE name = $name"
        .map(resultSet => UUID.fromString(resultSet.string("uuid")))
        .list()
        .apply()
        .headOption
    }

  def getVotePoint(uuid: UUID): Int =
    DB localTx { implicit session =>
      sql"SELECT vote_point FROM playerdata WHERE uuid = $uuid"
        .map(_.int("vote_point"))
        .list()
        .apply()
        .head
    }

  def setVotePoint(uuid: UUID, point: Int): Unit =
    DB localTx { implicit session =>
      sql"UPDATE playerdata SET vote_point=$point WHERE uuid = $uuid"
        .update()
        .apply()
    }

  def getRank(uuid: UUID): Option[Int] =
    DB localTx { implicit session =>
      sql"SELECT rank FROM playerdata WHERE uuid = $uuid"
        .map(_.int("rank"))
        .list()
        .apply()
        .headOption
    }

  def getExp(uuid: UUID): Option[Double] =
    DB localTx { implicit session =>
      sql"SELECT exp FROM playerdata WHERE uuid = $uuid"
        .map(_.double("exp"))
        .list()
        .apply()
        .headOption
    }
}
