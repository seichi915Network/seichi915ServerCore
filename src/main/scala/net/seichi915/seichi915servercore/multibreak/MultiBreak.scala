package net.seichi915.seichi915servercore.multibreak

case class MultiBreak(width: Int, height: Int, depth: Int) {
  require(width % 3 == 0 && height % 3 == 0 && depth % 3 == 0)

  def getWidth: Int = width

  def getHeight: Int = height

  def getDepth: Int = height
}
