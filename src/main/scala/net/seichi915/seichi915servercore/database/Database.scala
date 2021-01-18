package net.seichi915.seichi915servercore.database

import net.seichi915.seichi915servercore.Seichi915ServerCore
import net.seichi915.seichi915servercore.playerdata.PlayerData
import org.bukkit.entity.Player
import scalikejdbc._

import java.io.{File, FileOutputStream}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

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

  def getPlayerData(player: Player): Future[Option[PlayerData]] = Future {
    val playerDataList = DB localTx { implicit session =>
      sql"SELECT total_break_amount FROM playerdata WHERE uuid = ${player.getUniqueId}"
        .map(resultSet => PlayerData(resultSet.long("total_break_amount")))
        .list()
        .apply()
    }
    playerDataList.headOption
  }

  def createNewPlayerData(player: Player): Future[Unit] = Future {
    DB localTx { implicit session =>
      sql"INSERT INTO playerdata (uuid, name, total_break_amount) VALUES (${player.getUniqueId}, ${player.getName}, 0)"
        .update()
        .apply()
    }
  }

  def savePlayerData(player: Player, playerData: PlayerData): Future[Unit] =
    Future {
      DB localTx { implicit session =>
        sql"UPDATE playerdata SET name=${player.getName}, total_break_amount=${playerData.getTotalBreakAmount} WHERE uuid = ${player.getUniqueId}"
          .update()
          .apply()
      }
    }

  def updatePlayerNameIfChanged(player: Player): Future[Unit] = Future {
    getPlayerData(player) onComplete {
      case Success(value) if value.nonEmpty =>
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
      case Failure(exception) => throw exception
      case _                  =>
    }
  }
}
