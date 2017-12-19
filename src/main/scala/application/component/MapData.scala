package application.component

import application.component.tiles.{DirectionInfo, Tile}

/**
  * マップ情報クラス
  *
  * @param width 画面幅
  * @param height 画面高さ
  * @param _arrangementSize 縱橫に配置するタイルの数 arrangementSize × arrangementSize となる
  */
class MapData(width: Int, height: Int, protected val _arrangementSize: Int) {
  protected val _tiles: Array[Array[Tile]] = Array.ofDim(_arrangementSize, _arrangementSize)
  protected var _tileSize: Int = 0

  makeMap(width, height)

  def arrangementSize = _arrangementSize
  def tiles = _tiles
  def tileSize = _tileSize

  def makeMap(width: Int, height: Int): Unit = {
    _tileSize = width / _arrangementSize
    for ( x <- 0 until _arrangementSize;
          y <- 0 until _arrangementSize ) {
      _tiles(x)(y) = makeTile(x, y)
    }
  }

  def makeTile(ver: Int, hol: Int): Tile = {
    var isUp, isRight, isBottom, isLeft = true

    // 端のタイルの補正
    if ( ver < 1 ) {
      isUp = false
    }
    if ( ver >= _arrangementSize - 1 ) {
      isBottom = false
    }
    if ( hol >= _arrangementSize - 1 ) {
      isRight = false
    }
    if ( hol < 1 ) {
      isLeft = false
    }
    new Tile(hol * _tileSize, ver * _tileSize, _tileSize, DirectionInfo(isUp, isRight, isBottom, isLeft))
  }

  def updateMap(width: Int, height: Int): Unit = {
    _tileSize = width / _arrangementSize
    for ( ver <- 0 until _arrangementSize;
          hol <- 0 until _arrangementSize ) {
      _tiles(ver)(hol).resize(hol * _tileSize, ver * _tileSize, _tileSize)
      _tiles(ver)(hol).draw
    }
  }

  /**
    * 指定のタイルの添字を取得
    *
    * @param tile
    * @return
    */
  def getTileIndex(tile: Tile): Option[Tuple2[Int, Int]] = {
    for ( ver: Int <- 0 until _tiles.length;
          hol: Int <- 0 until _tiles(ver).length  ) {
      if ( _tiles(ver)(hol) equals(tile) ) {
        return Some((ver, hol))
      }
    }

    None
  }
}
