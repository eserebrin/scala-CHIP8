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
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyEvent

object Chip8 extends JFXApp {
	stage = new JFXApp.PrimaryStage {
		title = "CHIP-8"
		scene = new Scene(64*12,32*12+100) {
			val canvas = new Canvas(width.value, height.value)
			content = canvas
			val g = canvas.graphicsContext2D

			var rom = mutable.Buffer[Char]()
			val instream = new BufferedInputStream(new FileInputStream(new File("Pong.ch8")))
			while(instream.available > 0) rom += instream.read.asInstanceOf[Char]

			var keysPressed = mutable.Set[KeyCode]()
			canvas.onKeyPressed = (e: KeyEvent) => {
				if(e.code == KeyCode.Space && cpu.Debugger.enabled) 
					cpu.processInstruction(cpu.fetchOpcode)
				keysPressed += e.code
			}
			canvas.onKeyReleased = (e: KeyEvent) => keysPressed -= e.code

			val cpu = new CPU
			cpu.memory = Array.fill[Char](0x200)(0x00) ++ rom ++ Array.fill[Char](0x1000 - rom.size - 0x200)(0x00)

			var oldT = 0L
			val loop = AnimationTimer(t => {
				if(t - oldT > 1e9 / 60) {
					cpu.processInput(keysPressed)
					if(!cpu.Debugger.enabled) cpu.processInstruction(cpu.fetchOpcode)
					cpu.Debugger.display(g)
					cpu.processGraphics(g)
					cpu.processTimers()
				}
				oldT = t
			})

			loop.start()
			canvas.requestFocus()

		}
	}
}