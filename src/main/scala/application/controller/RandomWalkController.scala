package application.controller

import java.awt.Point
import java.io.IOException
import java.net.URL
import java.util.ResourceBundle
import javafx.animation.KeyFrame
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.Parent
import javafx.util.Duration

import application.AppLauncher
import application.component.MapData
import application.component.units.RandomUnit

import scalafx.Includes._
import scalafx.animation.Timeline
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane

/**
  * コントラーラクラスのオブジェクトクラス
  */
object RandomWalkController {
  private val FXMLFileName = "fxml/random_walk_controller.fxml"  // FXMLファイルの場所
  private val RandomWalkerFXMLLoader = getLoader      // FXMLLoader

  val Scene = new Scene(RandomWalkerFXMLLoader.getRoot[Parent])  // 指定のFXMLファイルのシーン
  // RandomWalkControllerのインスタンス
  val Instance: RandomWalkController = RandomWalkerFXMLLoader.getController[RandomWalkController]

  /**
    * FXMLLoaderの取得
    * @return
    */
  private def getLoader: FXMLLoader = {
    val fLoader: FXMLLoader = new FXMLLoader(ClassLoader.getSystemResource(FXMLFileName))

    try {
      fLoader.load()
    } catch {
      case e: IOException => e.printStackTrace
    }

    fLoader
  }
}

class RandomWalkController extends Initializable {
  @FXML private var drawPaneBase: javafx.scene.layout.Pane = null
  @FXML private var drawPane: javafx.scene.layout.Pane = null
  @FXML private var startButton: javafx.scene.control.Button = null
  private var drawPaneBaseS: Pane = null
  private var drawPaneS: Pane = null
  private var startButtonS: Button = null

  private var arrangementSize = 10 // 縱橫に配置するタイルの数 MapSize × MapSize となる
  private var numberOfRandomUnit = 10 // 移動ユニットの数

  private var mapData: MapData = null // マップデータ
  private var startPoint: Point = new Point(3, 3)  // 移動ユニットの開始位置
  private var moveCountOfRandUnit = 10             // 移動ユニットの移動可能回数
  private var randomUnits: Array[RandomUnit] = null // 移動ユニット

  private var animation: Timeline = null // アニメーション用
  private val AnimationInterval = 33     // アニメーション間隔

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    drawPaneBaseS = new Pane(drawPaneBase)
    drawPaneS = new Pane(drawPane)

    //== タイルの配置
    mapData = new MapData(drawPaneBase.getWidth.toInt, drawPaneBase.getHeight.toInt, arrangementSize)

    //== パネルへタイルの登録
    mapData.tiles.flatten.foreach(tile => drawPaneS.children.add(tile))

    // drawPaneBaseの画面サイズが変更されたときのイベントを設定
    drawPaneBase.height.onChange((source, oldValue, newValue) => {
      // パネルの更新
      updatePanelInfo
    })
    // drawPaneBaseの画面サイズが変更されたときのイベントを設定
    drawPaneBase.width.onChange((source, oldValue, newValue) => {
      // パネルの更新
      updatePanelInfo
    })
  }

  /**
    * パネルの更新
    */
  private def updatePanelInfo = {
    //== タイルの更新
    mapData.updateMap(drawPaneBaseS.getWidth.toInt, drawPaneBaseS.getHeight.toInt)

    //== 移動ユニットサイズの更新
    // タイルサイズの1/3のサイズに
    RandomUnit.unitSize_=((mapData.tiles(0)(0).width.get / 3).asInstanceOf[Int])

    val MapSize = arrangementSize * mapData.tileSize // マップの画面サイズの算出

    //== 描画パネルのサイズ変更
    changeDrawPaneSize(MapSize)
  }

  /**
    * ランダムウォークの開始メソッド
    */
  private def startRandomWalk = {
//    println(drawPaneS.children.length)
    //== 移動ユニットの描画パネルからのクリア
    drawPaneS.children.removeIf(unit => unit.isInstanceOf[RandomUnit])

    //== 移動ユニットの作成
    randomUnits = new Array(numberOfRandomUnit)
    for ( num <- 0 until numberOfRandomUnit ) {
      randomUnits(num) = new RandomUnit(mapData, startPoint.x, startPoint.y, moveCountOfRandUnit)
      //= 移動ユニットのパネルへの登録
      drawPaneS.children.add(randomUnits(num))
    }

    //== ランダムウォーク アニメーション処理
    if ( animation != null ) { animation.stop }  // 以前のアニメーションの停止
    animation = new Timeline(new javafx.animation.Timeline(new KeyFrame(Duration.millis(AnimationInterval), (event: ActionEvent) => {
      randomUnits.foreach(unit => unit.move(mapData))  // 全ての移動ユニットの移動
    })))

    animation.setCycleCount(Timeline.Indefinite)  // 処理のループ設定
    animation.play  // 開始
  }

  /**
    * 描画パネルのサイズ変更
    *
    * @param MapSize
    */
  private def changeDrawPaneSize(MapSize: Int) = {
    drawPaneS.setPrefWidth(MapSize)
    drawPaneS.setPrefHeight(MapSize)
    drawPaneS.setLayoutX((drawPaneBaseS.getWidth - MapSize) / 2.0)
    drawPaneS.setLayoutY((drawPaneBaseS.getHeight - MapSize) / 2.0)
  }

  /**
    * シーンの表示メソッド
    */
  def show: Unit = {
    AppLauncher.stage.scene = RandomWalkController.Scene
  }

  /**
    * スタートボタンをクリックしたとき
    *
    * @param event
    */
  def OnClickedStart(event: ActionEvent): Unit = {
    startRandomWalk
  }
}
