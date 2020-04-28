package emulator.chip8

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

class CPU(var memory:Array[Char] = Array.fill(0x1000)(0x00)) {

    implicit def chToInt(ch:Char): Int = ch.toInt
    implicit def intToCh(int:Int): Char = int.toChar
    implicit def intToBool(int:Int): Boolean = {
        if(int == 0) false
        else true
    }

    private var V = Array.fill[Char](16)(0x00)
    private var I:Char = 0x00
    private var PC:Char = 0x00
    private var soundTimer = 0
    private var delayTimer = 0
    private var keys = Array.fill[Char](16)(0x00)
    private var gfx = Array.fill(64*32)(false)

    V(0) = 32
    V(1) = 16
    memory(0x0000) = 0xAA
    memory(0x0001) = 0x55
    memory(0x0002) = 0xAA
    memory(0x0003) = 0x55
    memory(0x0004) = 0xD0
    memory(0x0005) = 0x13 // draw a 4px height sprite at (32,16)

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

    def fetchOpcode(): Char = {
        var hb = 0x0000
        var lb = 0x0000
        for(i <- 1 to 2) {
            // println(PC.toHexString.toUpperCase + "   " + memory(PC).toHexString.toUpperCase)
            if(i == 1) hb = memory(PC) << 8
            else lb = memory(PC)
            if(PC < 0x999) PC += 1
        }
        hb | lb
    }

    def processInstruction(opcode: Char): Unit = opcode match {

        case 0x00E0 => gfx = Array.fill(64*32)(false)

        case d if (d >= 0xD000 && d <= 0xDFFF) => {
            val x = V((opcode & 0x0F00) >>> 8)
            val y = V((opcode & 0x00F0) >>> 4)
            val n = opcode & 0x000F
            val oldI = I
            for(w <- 0 until 8; h <- 0 until n) {
                val spr = memory(I) & (0x10 >>> w)
                val npx = spr ^ gfx(I)
                println(npx + " " + spr.toBinaryString)
                val loc = (y - h - 1) * 64 + x + w
                gfx(loc) = npx
                if(intToBool(spr) != npx) V(0xF) = 1
                else V(0xF) = 0
                I += 1
            }
            I = oldI
        }

        case _ =>

    }
}