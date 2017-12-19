# 概要

[ScalaFXでランダムウォーク](http://hgmn-b.hatenablog.com)で紹介した**ScalaFXのランダムウォークアプリケーション**。

# 操作方法

アプリケーション内の操作タブ中のスタートを押すと、移動物体が10個、移動回数10回のランダムウォークが開始される。

# 動作確認済環境

- Windows10
- Scala (version: 2.12.4)
- ScalaFX (version: 8.0.144-R12)
- Java (version: 1.8.0_151)
- sbt (version: 1.0.4)

# jarファイルの生成

sbt-assemblyを用いて、jarファイルを作成する。  
sbt コンソール上で`assemby`コマンドを実行すると、コンパイルが行われ、プロジェクト内の指定のディレクトリにjarファイルが生成される。
