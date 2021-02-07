package net.seichi915.seichi915servercore.multibreak

case class MultiBreak(width: Int, height: Int, depth: Int) {
  require(width % 2 == 1 && height % 2 == 1 && depth % 2 == 1)

  def getWidth: Int = width

  def getHeight: Int = height

  def getDepth: Int = depth
}
