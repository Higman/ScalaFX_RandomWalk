package application

import javafx.scene.image.Image

import application.controller.RandomWalkController

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

object AppLauncher extends JFXApp {
  private val IconName = "images/kuma_icon.png"
  stage = new PrimaryStage {
    title = "RandomWalk"
    icons.add(new Image(ClassLoader.getSystemResource(IconName).toString))
  }

  RandomWalkController.Instance.show
}
