package emulator.chip8

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scala.util.Random
import scala.collection.mutable
import scalafx.scene.input.KeyCode

class CPU(private var memory:Array[Char] = Array.fill(0x1000)(0x00)) {

	var debugger = true // set to true to use spacebar to step one clock cycle

	implicit def chToInt(ch:Char): Int = ch.toInt
	implicit def intToCh(int:Int): Char = int.toChar
	implicit def intToBool(int:Int): Boolean = {
		if(int == 0) false
		else true
	}

	private var V = Array.fill[Char](16)(0x00)
	private var I:Char = 0x000
	private var PC:Char = 0x200
	private var soundTimer = 60
	private var delayTimer = 60
	private var keys = Array.fill[Boolean](16)(false)
	private var gfx = Array.fill(64*32)(false)
	private object Stack {
		private var arr = Array.fill[Char](16)(0x0000)
		private var sp = 0

		def push(adr: Char): Unit = {
			sp += 1
			arr(sp) = adr
		}

		def pop(): Char = {
			var ret = arr(sp)
			sp -= 1
			ret
		}
	}

	// *** BEGIN TESTING CODE *** 

	// V(0) = 32
	// V(1) = 16
	// memory(0x000) = 0xAA
	// memory(0x001) = 0x55
	// memory(0x002) = 0xAA
	// memory(0x003) = 0x55
	// memory(0x004) = 0xD0
	// memory(0x005) = 0x14 // draw a 4px height sprite at (32,16)

	// memory(0x000) = 0x14
	// memory(0x001) = 0x44 // jump to adress 0x444

	// memory(0x000) = 0x24
	// memory(0x001) = 0x44 // call subroutine at adress 0x444
	// memory(0x500) = 0x00
	// memory(0x501) = 0xEE // return

	// memory(0x000) = 0x60
	// memory(0x001) = 0x77 // store 0x77 to V(0)
	// memory(0x002) = 0x70
	// memory(0x003) = 0x01 // add one to V(0)
	// memory(0x004) = 0x61
	// memory(0x005) = 0x88 // store 0x88 to V(1)
	// memory(0x006) = 0x4

	// *** END OF TESTING CODE *** 

	def processGraphics(g:GraphicsContext): Unit = {
		g.setFill(Color.Black)
		g.fillRect(0,0,64*8,32*8)
		var x = 0
		var y = 0
		for (i <- 0 until gfx.size) {
			x = i % 64
			y = i / 64
			g.setFill(Color.White)
			if(gfx(i)) g.fillRect(x*8, y*8, 8, 8)
		}
	}

	def processInput(keysPressed: mutable.Set[KeyCode]): Unit = {
		for (key <- keysPressed) {
			key match {
				case KeyCode.Digit0 => keys(0x0) = true
				case KeyCode.Digit1 => keys(0x1) = true
				case KeyCode.Digit2 => keys(0x2) = true
				case KeyCode.Digit3 => keys(0x3) = true
				case KeyCode.Digit4 => keys(0x4) = true
				case KeyCode.Digit5 => keys(0x5) = true
				case KeyCode.Digit6 => keys(0x6) = true
				case KeyCode.Digit7 => keys(0x7) = true
				case KeyCode.Digit8 => keys(0x8) = true
				case KeyCode.Digit9 => keys(0x9) = true
				case KeyCode.A		=> keys(0xA) = true
				case KeyCode.B		=> keys(0xB) = true
				case KeyCode.C		=> keys(0xC) = true
				case KeyCode.D		=> keys(0xD) = true
				case KeyCode.E		=> keys(0xE) = true
				case KeyCode.F		=> keys(0xF) = true
				case _ =>
			}
		}
	}

	def processTimers(): Unit = {
		if(delayTimer > 0) {
			soundTimer -= 1
			delayTimer -=1
		}
		else {
			soundTimer = 60
			delayTimer = 60
		}
	}

	def fetchOpcode(): Char = {
		var hb = 0x0000
		var lb = 0x0000
		for(i <- 1 to 2) {

			print(PC.toHexString.toUpperCase + "  " + memory(PC).toHexString.toUpperCase)
			print("  |  ")
			for(i <- 0 until V.size) print("V" + i.toHexString.toUpperCase + ":" + V(i).toHexString.toUpperCase + " ")
			println(" |  I: " + I.toHexString.toUpperCase)
			println

			if(i == 1) hb = memory(PC) << 8
			else lb = memory(PC)
			if(PC < 0x999) PC += 1
		}
		hb | lb
	}

	def processInstruction(opcode: Char): Unit = opcode match {

		case 0x00E0 => gfx = Array.fill(64*32)(false) // clear screen

		case 0x00EE => PC = Stack.pop() // return

		case jmp if (opcode >= 0x1000 && opcode <= 0x1FFF) => PC = opcode & 0x0FFF

		case call if (opcode >= 0x2000 && opcode <= 0x2FFF) => {
			Stack.push(PC)
			PC = opcode & 0x0FFF
		}

		case math if (opcode >= 0x3000 && opcode <= 0x7FFF) => {
			val op = (opcode & 0xF000) >>> 12
			val x = (opcode & 0x0F00) >>> 8 
			val y = (opcode & 0x00F0) >>> 4
			val n = opcode & 0x00FF
			op match {
				case 0x3 => if(V(x) == n) PC += 2
				case 0x4 => if(V(x) != n) PC += 2
				case 0x5 => if(V(x) == V(y)) PC += 2
				case 0x6 => V(x) = n 
				case 0x7 => V(x) += n
				case _ 	 =>
			}
		}

		case bitOps if (opcode >= 0x8000 && opcode <= 0x8FFF) => {
			val x = (opcode & 0x0F00) >>> 8
			val y = (opcode & 0x00F0) >>> 4
			val op = opcode & 0x000F
			V(x) = op match {
				case 0x0 => V(y)
				case 0x1 => V(x) | V(y)
				case 0x2 => V(x) & V(y)
				case 0x3 => V(x) ^ V(y)
				case 0x4 => V(x) + V(y)
				case 0x5 => V(x) - V(y)
				case 0x6 => {
					V(0xF) = V(x) & 0x01
					V(x) >> 1
				}
				case 0x7 => V(y) - V(x)
				case 0xE => {
					V(0xF) = (V(x) & 0x80) >>> 7
					V(x) << 1
				}
				case _ 	 => V(x)
			}
			if(V(x) > 0xFF) V(0xF) = 1
			if(V(x) < 0x00) V(0xF) = 0
		}

		case setI if (opcode >= 0xA000 && opcode <= 0xAFFF) => I = opcode & 0x0FFF

		case jmpV0 if (opcode >= 0xB000 & opcode <= 0xBFFF) => PC = (opcode & 0x0FFF) + V(0)

		case rand if (opcode >= 0xC000 & opcode <= 0xCFFF) => {
			val x = (opcode & 0x0F00) >>> 8
			val n = opcode & 0x00FF
			V(x) & Random.nextInt((0 - 255) + 1)
		}

		case dispSpr if (opcode >= 0xD000 && opcode <= 0xDFFF) => {
			val x = V((opcode & 0x0F00) >>> 8)
			val y = V((opcode & 0x00F0) >>> 4)
			val n = opcode & 0x000F
			val oldI = I
			for (h <- 0 until n; w <- 0 until 8) {
				val spr = intToBool(memory(I) & (0x80 >>> w))
				val npx = spr ^ gfx(I)
				val loc = (y + h) * 64 + x + w
				gfx(loc) = npx
				// println("h: " + h + ", w: " + w + ", " + gfx(loc))
				if (spr != npx) V(0xF) = 1
				else V(0xF) = 0
				if(w == 7) I += 1
			}
			I = oldI
		}

		case keyOps if (opcode >= 0xE000 && opcode <= 0xEFFF) => {
			val x = (opcode & 0x0F00) >>> 8
			val op = opcode & 0x00FF
			if(op == 0x9E && keys(V(x))) PC += 2
			if(op == 0xA1 && !keys(V(x))) PC += 2
		}

		case fx if (opcode >= 0xF000 && opcode <= 0xFFFF) => {
			val x = (opcode & 0x0F00) >>> 8
			val op = opcode & 0x00FF
			op match {
				case 0x07 => V(x) = delayTimer
				case 0x0A => 
				case 0x15 => delayTimer = V(x)
				case 0x18 => soundTimer = V(x)
				case 0x1E => {
					I += V(x)
					if(I > 0xFFF) V(0xF) = 1
					else V(0xF) = 0
				}
				case 0x29 => 
				case 0x33 =>
				case 0x55 =>
				case 0x65 =>
				case _ =>
			}
		}

		case _ =>

	}
}