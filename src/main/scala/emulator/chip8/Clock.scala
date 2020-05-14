package emulator.chip8

class Clock {

	var soundTimer = 60
	var delayTimer = 60

	def processTimers(): Unit = {
		if(delayTimer > 0) {
			soundTimer -= 1
			delayTimer -= 1
		}
		else {
			soundTimer = 60
			delayTimer = 60
		}
    }
    
}