package emulator.nes

class CPU(cartridge: Cartridge) {
  val CPU_CLOCK_SPEED = 1789773 //TODO: implement clock

  private var PC: Int = 0xFFFC
  implicit def hex2int(hex: String): Int = Integer.parseInt(hex, 16)

  PC = cartridge.rom(PC + 1).toHexString + cartridge.rom(PC).toHexString // Reset Vector

  while (true) {
    if (scala.io.StdIn.readLine == "") {
      if (PC >= 0xFFFA) {
        System.exit(0)
      }
      println(PC.toHexString + "   " + cartridge.rom(PC).toHexString)
      executeInstructions(cartridge.rom(PC))
    }
  }

  def readAddress(lb: Short, hb: Short): Int = {
    cartridge.rom(hb).toHexString + cartridge.rom(lb).toHexString
  }

  private object Instructions {
    def NOP: Unit = {
      PC += 1
    }
    def JMP: Unit = {
      PC += 1
      var lb = cartridge.rom(PC)
      PC += 1
      var hb = cartridge.rom(PC)
      PC = readAddress(lb, hb)
    }
  }

  def executeInstructions(opcode: Short): Unit = opcode match {
    case 0xEA => Instructions.NOP
    case 0x4C => Instructions.JMP
    case _    =>
  }
}
