package eserebrin.emulator

import java.io.File
import scala.io.Source

class Cartridge() {
  // val fp = Source.fromFile("test.txt")
  // val rom: Array[Byte] = fp.mkString.split("\\s+").map(_.toByte)
  val rom = Array.fill(0xFFFF)(0xEA.toShort)
}
