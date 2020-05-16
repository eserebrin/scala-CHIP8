package emulator.chip8

class Clock {

	var soundTimer = 0
	var delayTimer = 0

	def processTimers(): Unit = {
		if(soundTimer > 0) println("BEEP")
		if(delayTimer > 0) delayTimer -= 1
		if(soundTimer > 0) soundTimer -= 1
    }
    
}