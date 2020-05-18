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

			val memory = (new Memory("Breakout.ch8")).create()
			val keyboard = new Keyboard
			val clock = new Clock
			val cpu = new CPU(memory, keyboard, clock)
			val debugger = new Debugger(cpu, memory, keyboard, clock)

			keyboard.defineInputFunction(canvas, debugger)

			// NOTE: AnimationTimer self regulates to ~60hz, resulting in the emulator running slow.
			// 		I'm not sure how to fix this, as the clock should be ~500hz.
			var oldT = 0L
			val loop = AnimationTimer(t => {
				if(t - oldT > 1e9 / 500) {
					// println((t - oldT) / 1e9)
					if(!debugger.enabled)
						cpu.processInstruction(cpu.fetchOpcode(debugger))
					if(cpu.draw) cpu.processGraphics(g)
					keyboard.update()
					debugger.display(g)
				}
				if(t - oldT > 1e9 / 60) clock.processTimers()
				oldT = t
			})

			// var t = System.nanoTime()
			// while(true) {
			// 	t = System.nanoTime()
			// 	if(t - oldT > 1e9 / 500) {
			// 		if(!debugger.enabled)
			// 			cpu.processInstruction(cpu.fetchOpcode(debugger))
			// 		if(cpu.draw) cpu.processGraphics(g)
			// 		keyboard.update()
			// 		debugger.display(g)
			// 	}
			// 	if(t - oldT > 1e9 / 60) clock.processTimers()
			// 	oldT = t
			// }

			loop.start()
			canvas.requestFocus()

		}
	}
}