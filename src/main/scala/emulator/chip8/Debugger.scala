package emulator.chip8

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

object Debugger extends Hardware {

	var enabled = false
	private var output = ""
	
	def display(g: GraphicsContext): Unit = {
		val screenWidth = 64*12
		val screenHeight = 32*12
		g.setFill(Color.Gray)
		g.fillRect(0,screenHeight,screenWidth,100)
		g.setFill(Color.Blue)
		g.setFont(new Font("Impact", 20))
		g.fillText(output, 0, screenHeight+50)
	}

	def run(): Unit = {
		output = PC.toHexString.toUpperCase + "  " + memory(PC).toHexString.toUpperCase + "  |  "
		for(i <- 0 until V.size) output += "V" + i.toHexString.toUpperCase + ":" + V(i).toHexString.toUpperCase + " "
		output += " |  I: " + I.toHexString.toUpperCase
	}

}