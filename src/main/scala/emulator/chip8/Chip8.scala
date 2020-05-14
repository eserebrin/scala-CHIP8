package emulator.chip8

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.animation.AnimationTimer

object Chip8 extends JFXApp {
	stage = new JFXApp.PrimaryStage {
		title = "CHIP-8"
		scene = new Scene(64*12,32*12+100) {
			val canvas = new Canvas(width.value, height.value)
			content = canvas
			val g = canvas.graphicsContext2D

			private val memory = (new Memory("Keypad Test.ch8")).create
			private val keyboard = new Keyboard(canvas)
			private val clock = new Clock
			private val cpu = new CPU(memory, keyboard, clock)
			private val debugger = new Debugger(cpu, memory, keyboard, clock)

			keyboard.processInput()

			var oldT = 0L
			val loop = AnimationTimer(t => {
				if(t - oldT > 1e9 / 60) {
					if(!debugger.enabled && !keyboard.waitingForInput)
						cpu.processInstruction(cpu.fetchOpcode(debugger))
					cpu.processGraphics(g)
					keyboard.update()
					debugger.display(g)
					clock.processTimers()
				}
				oldT = t
			})

			loop.start()
			canvas.requestFocus()

		}
	}
}