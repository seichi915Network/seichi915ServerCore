package net.seichi915.seichi915servercore.database

import net.seichi915.seichi915servercore.Seichi915ServerCore
import scalikejdbc._

import java.io.{File, FileOutputStream}

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
}
