package application.component.units

/**
  * 移動ユニットから見た方向
  */
object DirectionOfRandomUnit {
  final case object FRONTWARD extends DirectionOfRandomUnit(0)  // 前方
  final case object RIGHT extends DirectionOfRandomUnit(1)  // 右方向
  final case object BACKWARD extends DirectionOfRandomUnit(2)  // 後方
  final case object LEFT extends DirectionOfRandomUnit(3)  // 左方向

  val values = Array(FRONTWARD, RIGHT, BACKWARD, LEFT)
}

sealed abstract class DirectionOfRandomUnit(val number: Int)
