package eserebrin.emulator

class CPU(cartridge: Cartridge) {
  var PC = 0x0000 // eventually: 0xFFFC

  while (true) {
    if (PC >= 0xFFFF) {
      System.exit(0)
    }
    executeInstructions(cartridge.rom(PC))
    PC += 1
    println(PC.toHexString)
  }

  object Instructions {
    def NOP: Unit = {}
  }

  def executeInstructions(opcode: Byte): Unit = opcode match {
    case 0xEA => Instructions.NOP
  }
}
