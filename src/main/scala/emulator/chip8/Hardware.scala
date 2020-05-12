package emulator.chip8

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

abstract class Hardware {

    var memory:Array[Char] = Array.fill(0x1000)(0x00)
	var V = Array.fill[Char](16)(0x00)
	var I:Char = 0x000
	var PC:Char = 0x200
	var soundTimer = 60
	var delayTimer = 60
	var keys = Array.fill[Boolean](16)(false)
    var gfx = Array.fill(64*32)(false)

	object Stack {
        var arr = Array.fill[Char](16)(0x0000)
        var sp = 0
		def push(adr: Char): Unit = {
			sp += 1
			arr(sp) = adr
		}
		def pop(): Char = {
			var ret = arr(sp)
			sp -= 1
			ret
		}
		def peek(): Char = arr(sp)
	}
	
	object Debugger {

		var enabled = false

		private var instruction = ""
		private var vkeys = ""
		private var vvals = ""
		private var output = ""
		private var index = ""
		private var stkP = ""
		private var delay = ""
		private var sound = ""
		private var mem = ""

		def formatHex8(in: Char): String = {
			val h = in.toHexString.toUpperCase
			if(h.length == 1) "0" + h
			else h
		}

		def formatHex16(in: Char): String = {
			val h = in.toHexString.toUpperCase
			if(h.length == 1) "000" + h
			else if(h.length == 2) "00" + h
			else if(h.length == 3) "0" + h
			else h
		}
	
		def display(g: GraphicsContext): Unit = {
			val screenWidth = 64*12
			val screenHeight = 32*12

			g.setFill(Color.Gray)
			g.fillRect(0,screenHeight,screenWidth,100)

			g.setFill(Color.Blue)
			g.setFont(new Font("Courier", 24))
			g.fillText(instruction, 10, screenHeight+30)

			g.setFont(new Font("Courier", 16))
			g.fillText(vkeys, 10, screenHeight+70)
			g.fillText(vvals, 10, screenHeight+85)

			g.setFont(new Font("Courier", 18))
			g.fillText(index, 500, screenHeight+65)
			g.fillText(stkP, 500, screenHeight+85)

			g.fillText(delay, 620, screenHeight+65)
			g.fillText(sound, 620, screenHeight+85)
		}

		def run(): Unit = {
			instruction = formatHex16((PC-1).toChar) + "-" + formatHex16(PC) + ": " + formatHex8(memory(PC-1)) + formatHex8(memory(PC))
			vkeys = ""
			vvals = ""
			for(i <- 0 until V.size) vkeys += "V" + i.toHexString.toUpperCase + " "
			for(i <- 0 until V.size) vvals += formatHex8(V(i)) + " "
			index = " I: " + formatHex16(I)
			stkP = "SP: " + formatHex16(Stack.peek)
			delay = "DELAY:" + delayTimer
			sound = "SOUND:" + soundTimer
			// for(i <- I.toInt until I+16) print(formatHex8(memory(i)) + " ")
			// println
		}

	}

}