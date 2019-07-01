package scalareplserver

import java.io.{ BufferedReader, InputStreamReader, PrintWriter }

object ScalaReplServer {
  def main(args: Array[String]): Unit = {
    val in = new BufferedReader(new InputStreamReader(System.in))
    val out = new PrintWriter(System.out, /* autoFlush = */ true)
    val repl = new Repl(in, out)
    repl.run()
  }
}

final class Repl(in: BufferedReader, out: PrintWriter) {
  def run(): Unit = {
    out.println("Connected to a fresh REPL")
    var userInput: String = null
    while ({
      out.print("scala> ")
      out.flush()
      userInput = in.readLine
      userInput != null
    }) {
      out.println(s"echo: $userInput")
    }
    out.println()
  }
}
