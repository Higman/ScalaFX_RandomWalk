package application.component.units

import java.awt.geom.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

import application.component.tiles.Tile
import application.component.{Direction, MapData}

import scala.util.Random

object RandomUnit {
  private var _unitSize: Int = 10 // ユニットのサイズ
  private var _moveSpeed: Int = 5 // ユニットの移動速度
  private val MinProgressOfMoving = 0  // タイル間の移動の進行度の最小
  private val MaxProgressOfMoving = 100  // タイル間の移動の進行度の最大

  def unitSize = _unitSize
  def moveSpeed = _moveSpeed

  def unitSize_=(newSize: Int): Unit = {
    _unitSize = newSize
  }

  def moveSpeed_=(newMoveSpeed: Int): Unit = {
    _moveSpeed = newMoveSpeed
  }
}

/**
  * ランダムウォークを行う移動ユニットクラス
  *
  * @param tileVerIdx         座標
  * @param tileHolIdx         座標
  * @param moveCount 可能移動回数
  * @param color     色
  */
class RandomUnit(mapData: MapData, tileVerIdx: Int, tileHolIdx: Int, protected var moveCount: Int, color: Color) extends Circle {

  protected var previousTile: Tile = null // 1つ前にいたタイル
  protected var currentTile: Tile = null  // 現在いるタイル
  protected var progressOfMoving = RandomUnit.MinProgressOfMoving  // 現在のタイル間の移動の進行度
  protected var _isActive = true  // 稼動状態にあるかどうか（移動可能回数が1以上であるかどうか）

  def isActive = _isActive

  previousTile = mapData.tiles(tileVerIdx)(tileHolIdx)
  setLayoutX(previousTile.getLayoutX + previousTile.getWidth / 2)
  setLayoutY(previousTile.getLayoutY + previousTile.getHeight / 2)
  setRadius(RandomUnit.unitSize)
  setFill(color)

  initTile(mapData)

  /**
    * 補助コンストラクタ
    *
    * @param tileVerIdx         座標
    * @param tileHolIdx         座標
    * @param moveCount 可能移動回数
    */
  def this(mapData: MapData, tileVerIdx: Int, tileHolIdx: Int, moveCount: Int) {
    this(mapData, tileVerIdx, tileHolIdx, moveCount, Color.BLACK)
    // 色のランダム設定
    val rand: Random = new Random()
    setFill(Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)))
  }

  /**
    * 移動ユニットの移動
    *
    * @param mapData
    */
  def move(mapData: MapData): Unit = {
    //=== 状態判定
    if ( moveCount < 1 ) { _isActive = false; return  }

    //=== 前のタイルと現在のタイルの位置の取得
    val previousPosition = new Point2D.Double(previousTile.layoutX.get, previousTile.layoutY.get)
    val currentPosition = new Point2D.Double(currentTile.layoutX.get, currentTile.layoutY.get)

    //=== 移動量の算出
    // タイル間を移動する際の更新回数
    val numberOfUpdateProgress = (RandomUnit.MaxProgressOfMoving - RandomUnit.MinProgressOfMoving) / RandomUnit._moveSpeed
    // 移動量
    val dMoveX = (currentPosition.getX - previousPosition.getX) / numberOfUpdateProgress
    val dMoveY = (currentPosition.getY - previousPosition.getY) / numberOfUpdateProgress

    //=== 移動
    setLayoutX(getLayoutX+dMoveX)
    setLayoutY(getLayoutY+dMoveY)

    //=== 更新
    progressOfMoving += RandomUnit._moveSpeed

    //=== 次のタイルの判定
    if ( progressOfMoving >= RandomUnit.MaxProgressOfMoving ) {
      moveCount -= 1  // 移動回数の更新

      //== 補正
      setLayoutX(currentTile.layoutX.get + currentTile.width.get / 2)
      setLayoutY(currentTile.layoutY.get + currentTile.height.get / 2)

      updateTile(mapData)  // 次のタイルの決定
      progressOfMoving = RandomUnit.MinProgressOfMoving // 初期化
    }
  }

  /**
    * 移動先タイル情報の初期化
    *
    * @param mapData
    */
  def initTile(mapData: MapData): Unit = {
    //== 初期タイルの移動可能性を取得
    val directionInfoOfStartPoint = previousTile.directionInfo

    //== 移動可能な進行方向のリストを生成
    val movableDirectionList =  Direction.values.zip(directionInfoOfStartPoint.getAll).collect {
      case (dir, true) => dir
    }

    //== 次に進む方向
    val nextDirection = Direction.atNumber(new Random().nextInt(movableDirectionList.length))

    //== 移動添字を取得
    val dIndex = nextDirectionIndex(nextDirection)

    currentTile = mapData.getTileIndex(previousTile) match {
      case Some((ver, hol)) => mapData.tiles(ver + dIndex._1)(hol + dIndex._2)
      case None => mapData.tiles(0)(0)
    }
  }

  /**
    * 指定の方向に移動するための配列添え字値を取得
    *
    * @param nextDirection
    * @return
    */
  private def nextDirectionIndex(nextDirection: Option[Direction]) = {
    nextDirection match {
      case Some(Direction.UP) => (-1, 0)
      case Some(Direction.DOWN) => (1, 0)
      case Some(Direction.RIGHT) => (0, 1)
      case Some(Direction.LEFT) => (0, -1)
      case None => (0, 0)
    }
  }

  def updateTile(mapData: MapData): Unit = {
    val dIndex = nextDirectionIndex(nextDirection(mapData))

    //=== 現在のタイルの配列添字
    val currentTileIndex = mapData.getTileIndex(currentTile)

    //=== 次に進むタイルを取得
    val nextTile = currentTileIndex match {
      case Some((ver, hol)) => mapData.tiles(ver + dIndex._1)(hol + dIndex._2)
      case None => currentTile
    }

    //=== 現在の状態を更新
    previousTile = currentTile
    currentTile = nextTile
  }

  def nextDirection(mapData: MapData): Option[Direction] = {
    val currentDirectionInfoList = currentTile.directionInfo.getAll  // 現在のタイルの移動可能方向

    //=== 移動ユニットの進行方向の取得
    val dirOfMoving: Direction = directionOfMoving(mapData) match {
      case Some(dir) => dir
      case None => return None  // 状態が正しくないとき（進行宝庫がないとき）
    }

    //=== 進行方向から、移動ユニットを基準としたそれぞれの方向の進行可能性を調べる
    val (before, after) = currentDirectionInfoList.splitAt(dirOfMoving.number)
    val directionOfRandomUnitInfoList: List[Boolean] = after ::: before

    //=== 各方向へ進む可能性を表すDirectionOfRandomUnitInfoクラスのインスタンスを作成
    val directionOfRandomUnitInfo = DirectionOfRandomUnitInfo(directionOfRandomUnitInfoList(0),
      directionOfRandomUnitInfoList(1),
      directionOfRandomUnitInfoList(2),
      directionOfRandomUnitInfoList(3))

    //=== 進行可能性から、各方向へ移動する確率を表すProbabilityOfDirectionOfRandomUnitクラスのインスタンスを作成
    val probOfDirOfRandUnit = directionOfRandomUnitInfo match {
      case DirectionOfRandomUnitInfo(true, true, true, true) => ProbabilityOfDirectionOfRandomUnit(0.5, 0.2 ,0.1, 0.2)
      case DirectionOfRandomUnitInfo(true, true, true, false) => ProbabilityOfDirectionOfRandomUnit(0.5, 0.4, 0.1, 0.0)
      case DirectionOfRandomUnitInfo(true, false, true, true) => ProbabilityOfDirectionOfRandomUnit(0.5, 0.0, 0.1, 0.5)
      case DirectionOfRandomUnitInfo(false, true, true, true) => ProbabilityOfDirectionOfRandomUnit(0.0, 0.4, 0.2, 0.4)
      case DirectionOfRandomUnitInfo(false, false, true, true) => ProbabilityOfDirectionOfRandomUnit(0.0, 0.0, 0.2, 0.8)
      case DirectionOfRandomUnitInfo(false, true, true, false) => ProbabilityOfDirectionOfRandomUnit(0.0, 0.8, 0.2, 0.0)
      case _ => return None
    }

    //=== 進行可能性の確率に基づいて、移動ユニットから見た進行方向を決定
    val nextDirOfRandUnit =  probOfDirOfRandUnit.getDirectionOfRandomUnit

    //=== 移動ユニットから見た進行方向(DirectionOfRandomUnit)を、画面を基準とした方向(Direction)に変換する
    // 前タイルからの移動方向と次のタイルへの移動方向の和を移動方向の種類の数で剰余をとる
    val nextDir = (dirOfMoving.number + nextDirOfRandUnit.number) % Direction.values.length

    Direction.atNumber(nextDir)  // 求めた数値に対応する方向を返却
  }

  /**
    * 移動ユニットの現在の進行方向を取得
    * @param mapData
    * @return
    */
  def directionOfMoving(mapData: MapData): Option[Direction] = {
    //== 例外判定
    if ( currentTile == null || previousTile == null || currentTile == previousTile ) {
      return None
    }

    //== 処理
    var dir: Option[Direction] = None
    val previousIndex = mapData.getTileIndex(previousTile)
    val currentIndex = mapData.getTileIndex(currentTile)

    previousIndex.foreach(preIdx => {
      currentIndex.foreach(curIdx => {
        (curIdx._1 - preIdx._1, curIdx._2 - preIdx._2) match {
          case (-1, 0) => dir = Some(Direction.UP)
          case (1, 0) => dir = Some(Direction.DOWN)
          case (0, 1) => dir = Some(Direction.RIGHT)
          case (0, -1) => dir = Some(Direction.LEFT)
          case _ => dir = None
        }
      })
    })

    return dir
  }
}

/**
  * 移動ユニットから見た、それぞれの方向に進む確率
  *
  * @param left      左方向に進む確率
  * @param backward  後ろ方に進む確率
  * @param frontward 前方に進む確率
  * @param right     右方向に進む確率
  */
sealed case class ProbabilityOfDirectionOfRandomUnit (frontward: Double, right: Double, backward: Double, left: Double) {
  /**
    * フィールドの各方向に進む確率に基づいて、進む方向をランダムに取得する
    */
  val rand = new Random()
  def getDirectionOfRandomUnit: DirectionOfRandomUnit = {
    val prob = rand.nextDouble()  // 0.0 ~ 1.0 の範囲の値を取得
    var totalProb: Double = frontward

    if ( prob < totalProb ) { return DirectionOfRandomUnit.FRONTWARD }
    totalProb += right
    if ( prob < totalProb ) { return DirectionOfRandomUnit.RIGHT }
    totalProb += backward
    if ( prob < totalProb ) { return DirectionOfRandomUnit.BACKWARD }

    DirectionOfRandomUnit.LEFT
  }
}

/**
  * 移動ユニットを基準とした方向フラグ管理クラス
  *
  * @param isFrontward
  * @param isRight
  * @param isBackward
  * @param isLeft
  */
sealed case class DirectionOfRandomUnitInfo(isFrontward: Boolean, isRight: Boolean, isBackward: Boolean, isLeft: Boolean) {
  def getAll = List(isFrontward, isRight, isBackward, isLeft)
}
