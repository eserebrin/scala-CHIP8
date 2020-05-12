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
	var keysPressed = mutable.Set[KeyCode]()
	
	val cpu = new CPU
	cpu.memory = createRom("Keypad Test.ch8")

	def createRom(file: String): Array[Char] = {
		val font = Array[Char](
			0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
			0x20, 0x60, 0x20, 0x20, 0x70, // 1
			0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
			0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
			0x90, 0x90, 0xF0, 0x10, 0x10, // 4
			0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
			0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
			0xF0, 0x10, 0x20, 0x40, 0x40, // 7
			0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
			0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
			0xF0, 0x90, 0xF0, 0x90, 0x90, // A
			0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
			0xF0, 0x80, 0x80, 0x80, 0xF0, // C
			0xE0, 0x90, 0x90, 0x90, 0xE0, // D
			0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
			0xF0, 0x80, 0xF0, 0x80, 0x80  // F
		)
		var rom = mutable.Buffer[Char]()
		val instream = new BufferedInputStream(new FileInputStream(new File(file)))
		while(instream.available > 0) rom += instream.read.asInstanceOf[Char]
		font ++ Array.fill[Char](0x200-0x50)(0x00) ++ rom ++ Array.fill[Char](0x1000 - rom.size - 0x200)(0x00)
	}

	def processKeys(canvas: Canvas): Unit = {
		canvas.onKeyPressed = (e: KeyEvent) => {
			if(e.code == KeyCode.Space && cpu.Debugger.enabled)
				cpu.processInstruction(cpu.fetchOpcode)
			keysPressed += e.code
		}
		canvas.onKeyReleased = (e: KeyEvent) => keysPressed -= e.code
	}

	def getKey(keys: Array[Boolean]): Char = {
		println("hey")
		if(keysPressed.isEmpty) Thread.sleep(10)
		keys.indexOf(true).toChar
	}

	stage = new JFXApp.PrimaryStage {
		title = "CHIP-8"
		scene = new Scene(64*12,32*12+100) {
			val canvas = new Canvas(width.value, height.value)
			content = canvas
			val g = canvas.graphicsContext2D

			processKeys(canvas)

			var oldT = 0L
			val loop = AnimationTimer(t => {
				if(t - oldT > 1e9 / 60) {
					if(!cpu.Debugger.enabled)
						cpu.processInstruction(cpu.fetchOpcode)
					cpu.processGraphics(g)
					cpu.processInput(keysPressed)
					cpu.Debugger.display(g)
					cpu.processTimers()
				}
				oldT = t
			})

			loop.start()
			canvas.requestFocus()

		}
	}
}