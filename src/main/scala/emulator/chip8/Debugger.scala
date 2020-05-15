package emulator.chip8

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.text.Font

//TODO: refactor this crap
class Debugger(cpu: CPU, memory: Array[Char], keyboard: Keyboard, clock: Clock) {

    var enabled = false

    private var instruction = ""
    private var vkeys = ""
    private var vvals = ""
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
        instruction = formatHex16((cpu.PC-1).toChar) + "-" +
                      formatHex16(cpu.PC) + ": " +
                      formatHex8(memory(cpu.PC-1)) + formatHex8(memory(cpu.PC))
        vkeys = ""
        vvals = ""
        for(i <- 0 until cpu.V.size) vkeys += "V" + i.toHexString.toUpperCase + " "
        for(i <- 0 until cpu.V.size) vvals += formatHex8(cpu.V(i)) + " "
        index = " I: " + formatHex16(cpu.I)
        stkP = "SP: " + formatHex16(cpu.Stack.peek)
        delay = "DELAY:" + clock.delayTimer
        sound = "SOUND:" + clock.soundTimer
        // for(i <- cpu.I.toInt until cpu.I+16) print(formatHex8(memory(i)) + " ")
        // println
    }

    def stepOnce(): Unit = cpu.processInstruction(cpu.fetchOpcode(this))
}