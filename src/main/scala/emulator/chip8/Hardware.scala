package emulator.chip8

abstract class Hardware {

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
    }

}