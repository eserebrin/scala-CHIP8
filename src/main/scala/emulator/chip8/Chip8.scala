package emulator.chip8

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.animation.AnimationTimer

object Chip8 extends JFXApp {

    implicit def chToInt(ch:Char): Int = ch.toInt
    implicit def intToCh(int:Int): Char = int.toChar

    stage = new JFXApp.PrimaryStage {
        scene = new Scene(64*8,32*8) {
            val canvas = new Canvas(width.value, height.value)
            content = canvas
            val g = canvas.graphicsContext2D

            val cpu = new CPU

            val timer = AnimationTimer(t => {
                cpu.processInstruction(cpu.fetchOpcode)
                cpu.processGraphics(g)
            })

            timer.start()
            canvas.requestFocus()

        }
    }
}