package application.component.tiles

import scalafx.scene.canvas.Canvas

object Tile {
  val UnitOfAngle = Math.toRadians(90) // 角度単位
  private val StartAngle = Math.toRadians(-90)  // 開始角度
}

class Tile(x: Int, y: Int, size: Int, protected val _directionInfo: DirectionInfo) extends Canvas {
  resize(x, y, size)  // サイズ設定

  draw // 描画

  def directionInfo = _directionInfo

  /**
    * 移動ユニットの描画
    */
  def draw: Unit = {
    val x1 = width.get / 2
    val y1 = height.get / 2

    _directionInfo.getAll.zipWithIndex.foreach((isDir) => {
      if ( isDir._1 ) {
        val currentAngle = Tile.StartAngle + isDir._2 * Tile.UnitOfAngle
        val x2: Double = x1 + width.get * Math.cos(currentAngle)
        val y2: Double = y1 + height.get * Math.sin(currentAngle)

        graphicsContext2D.strokeLine(x1, y1, x2, y2)
      }
    })
  }

  def resize(x: Int, y: Int, size: Int): Unit = {
    layoutX_=(x)
    layoutY_=(y)
    width_=(size)
    height_=(size)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Tile]

  override def equals(other: Any): Boolean = other match {
    case that: Tile =>
      super.equals(that) &&
        (that canEqual this) &&
        _directionInfo == that._directionInfo
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(super.hashCode(), _directionInfo)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

/**
  * 方向フラグ管理クラス
  *
  * @param isUp
  * @param isRight
  * @param isDown
  * @param isLeft
  */
sealed case class DirectionInfo(isUp: Boolean, isRight: Boolean, isDown: Boolean, isLeft: Boolean) {
  def getAll = List(isUp, isRight, isDown, isLeft)

  def atNumber(number: Int): Option[Boolean] = {
    number match {
      case 0 => Some(isUp)
      case 1 => Some(isRight)
      case 2 => Some(isDown)
      case 3 => Some(isLeft)
      case _ => None
    }
  }
}
