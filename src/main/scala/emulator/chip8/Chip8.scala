package emulator.chip8

import scalafx.application.JFXApp
import scalafx.application.JFXApp.Parameters
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
			val params = parameters.raw ++ Array("", "")

			val cartridge = if(params(0) != "") params(0) else "Pong.ch8"
			val memory = (new Memory(cartridge)).create()

			val keyboard = new Keyboard
			val clock = new Clock
			val cpu = new CPU(memory, keyboard, clock)
			
			val debugger = new Debugger(cpu, memory, keyboard, clock)
			debugger.enabled = if(params(1) == "debug") true else false

			keyboard.defineInputFunction(canvas, debugger)

			// Main loop runs 8 times every 60th of a second, roughly 500Hz
			var oldT = 0L
			val loop = AnimationTimer(t => {
				if(t - oldT > 1e9 / 60) {
					for(i <- 0 to 8) {
						if(!debugger.enabled)
							cpu.processInstruction(cpu.fetchOpcode(debugger))
						if(cpu.draw) cpu.processGraphics(g)
						keyboard.update()
						debugger.display(g)
					}
					clock.processTimers()
				}
				oldT = t
			})

			loop.start()
			canvas.requestFocus()

		}
	}
}