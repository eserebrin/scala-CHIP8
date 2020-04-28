package emulator.nes

import java.io.File
import scala.io.Source

class Cartridge() {
  // val fp = Source.fromFile("test.txt")
  // val rom: Array[Byte] = fp.mkString.split("\\s+").map(_.toByte)
  val rom = Array.fill(0xFFFF)(0xEA.toShort)

  rom(0xFFFC) = 0x00 
  rom(0xFFFD) = 0x00 // position reset vector at beginning

  rom(0x0000) = 0x4C
  rom(0x0001) = 0x34
  rom(0x0002) = 0x12 // JMP $1234
}
