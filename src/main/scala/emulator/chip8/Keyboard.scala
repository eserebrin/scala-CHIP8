package emulator.chip8

import scalafx.Includes._
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode
import scala.collection.mutable

class Keyboard(canvas: Canvas) {

    var keys: Array[Boolean] = Array.fill(16)(false)
	var keysPressed = mutable.Set[KeyCode]()
	var waitingForInput = false

	def getKeys(): Array[Boolean] = {
		var ret = Array.fill(16)(false)
		for(key <- keysPressed) key match {
			case KeyCode.Digit1	=> ret(0x1) = true
			case KeyCode.Digit2	=> ret(0x2) = true
			case KeyCode.Digit3	=> ret(0x3) = true
			case KeyCode.Digit4	=> ret(0xC) = true
			case KeyCode.Q		=> ret(0x4) = true
			case KeyCode.W		=> ret(0x5) = true
			case KeyCode.E		=> ret(0x6) = true
			case KeyCode.R		=> ret(0xD) = true
			case KeyCode.A		=> ret(0x7) = true
			case KeyCode.S		=> ret(0x8) = true
			case KeyCode.D		=> ret(0x9) = true
			case KeyCode.F		=> ret(0xE) = true
			case KeyCode.Z		=> ret(0xA) = true
			case KeyCode.X		=> ret(0x0) = true
			case KeyCode.C		=> ret(0xB) = true
			case KeyCode.V		=> ret(0xF) = true
			case _ =>
		}
		ret
    }

	def processInput(): Unit = {
		canvas.onKeyPressed = (e: KeyEvent) => {
			// if(e.code == KeyCode.Space) cpu.stepOnce
			keysPressed += e.code
		}
		canvas.onKeyReleased = (e: KeyEvent) => {
			keysPressed -= e.code
		}
	}

	def getKey(): Char = { // for use in FX0A
		while(!keys.contains(true)) {
			waitingForInput = true
			processInput()
			update()
		}
		waitingForInput = false
		keys.indexOf(true).toChar
	}

	def update(): Unit = keys = getKeys()

}