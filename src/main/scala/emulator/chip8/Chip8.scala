package emulator.chip8

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.animation.AnimationTimer
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.File
import scala.collection.mutable
import java.io.DataInputStream
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyEvent

object Chip8 extends JFXApp {
	stage = new JFXApp.PrimaryStage {
		title = "CHIP-8"
		scene = new Scene(64*8,32*8) {
			val canvas = new Canvas(width.value, height.value)
			content = canvas
			val g = canvas.graphicsContext2D

			var rom = mutable.Buffer[Char]()
			val instream = new DataInputStream(new BufferedInputStream(new FileInputStream(new File("Pong.ch8"))))
			while(instream.available > 0) rom += instream.readChar

			var keysPressed = mutable.Set[KeyCode]()
			canvas.onKeyPressed = (e: KeyEvent) => keysPressed += e.code
			canvas.onKeyReleased = (e: KeyEvent) => keysPressed -= e.code

			val cpu = new CPU(Array.fill[Char](0x200)(0x00) ++ rom ++ Array.fill[Char](0x1000 - rom.size - 0x200)(0x00))

			var oldT = 0L
			val loop = AnimationTimer(t => {
				if(t - oldT > 1e9 / 60) {
					cpu.processInput(keysPressed)
					// cpu.processInstruction(cpu.fetchOpcode)
					cpu.processGraphics(g)
					cpu.decTimers()
				}
				oldT = t
				cpu.resetTimers()
				println(cpu.delayTimer)
			})

			loop.start()
			canvas.requestFocus()

		}
	}
}