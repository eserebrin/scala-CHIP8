package eserebrin.emulator

class CPU(cartridge: Cartridge) {
  val CPU_CLOCK_SPEED = 1789773 //TODO: implement clock

  private var PC: Int = 0xFFFC
  implicit def hex2int(hex: String): Int = Integer.parseInt(hex, 16)
  PC = hex2int(
    cartridge.rom(PC + 1).toHexString +
      cartridge.rom(PC).toHexString
  ) // Reset Vector

  while (true) {
    if (scala.io.StdIn.readLine == "") {
      if (PC >= 0xFFFA) {
        System.exit(0)
      }
      println(PC.toHexString + "   " + cartridge.rom(PC).toHexString)
      executeInstructions(cartridge.rom(PC))
      PC += 1
    }
  }

  object Instructions {
    def NOP: Unit = {}
  }

  def executeInstructions(opcode: Short): Unit = opcode match {
    case 0xEA => Instructions.NOP
  }
}
