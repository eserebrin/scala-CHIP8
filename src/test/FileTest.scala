import java.io.PrintWriter

object FileTest {
  def main(args: Array[String]) {
    val pw = new PrintWriter(new File("test.txt"))
    for (i <- 0 until 0xFFFF) {
      pw.write(0xEA + " ")
    }
    pw.close
  }
}
