package net.seichi915.seichi915servercore.external

import net.coreprotect.{CoreProtect, CoreProtectAPI}

object ExternalPlugins {
  def getCoreProtectAPI: CoreProtectAPI = CoreProtect.getInstance().getAPI
}
