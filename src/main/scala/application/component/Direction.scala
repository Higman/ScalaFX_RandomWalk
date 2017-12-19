package application.component

/**
  * 方向を表す列挙体
  */
object Direction {
  final case object UP extends Direction(0) // 上方向
  final case object RIGHT extends Direction(1) // 右方向
  final case object DOWN extends Direction(2) // 下方向
  final case object LEFT extends Direction(3) // 左方向

  val values = Array(UP, RIGHT, DOWN, LEFT)

  def atNumber(number: Int): Option[Direction] = {
    number match {
      case 0 => Some(UP)
      case 1 => Some(RIGHT)
      case 2 => Some(DOWN)
      case 3 => Some(LEFT)
      case _ => None
    }
  }
}

sealed abstract class Direction(val number: Int)
