package scalareplserver

import java.io.{ BufferedReader, InputStream, InputStreamReader, OutputStream, PrintWriter }
import java.net.{ ServerSocket, Socket, SocketException }
import java.util.concurrent.atomic.AtomicBoolean

object ReplServer {
  def main(args: Array[String]): Unit = {
    val serverSocket = new ServerSocket(4444)

    new Thread(new ReplServer(serverSocket), "ReplServer").start()

    ShellLoop.run(System.in, System.out) { out =>
      out.println(s"Started the REPL Server: $serverSocket")
      out.println("press Ctrl-D to stop the server accepting new connections")
    }(prompt = "")((_, _) => ())

    serverSocket.close()
  }
}

final class ReplServer(serverSocket: ServerSocket) extends Runnable {
  def run(): Unit = {
    try
      while (!serverSocket.isClosed)
        new Thread(new ReplRunnable(serverSocket.accept()), "Repl").start()
    catch {
      case e: SocketException if serverSocket.isClosed => ()
    }
  }
}

final class ReplRunnable(socket: Socket) extends Runnable {
  def run(): Unit = {
    ShellLoop.run(socket.getInputStream, socket.getOutputStream) { out =>
      out.println("Connected to a fresh REPL")
    } (prompt = "scala> ") { (out, userInput) =>
      out.println(s"echo: $userInput")
    }
    socket.close()
  }
}

object ShellLoop {
  def run(in0: InputStream, out0: OutputStream)
      (intro: PrintWriter => Unit)
      (prompt: String)
      (process: (PrintWriter, String) => Unit)
  : Unit = {
    val in = new BufferedReader(new InputStreamReader(in0))
    val out = new PrintWriter(out0, /* autoFlush = */ true)

    intro(out)

    var userInput: String = null
    while ({
      if (prompt != "") {
        out.print(prompt)
        out.flush()
      }
      userInput = in.readLine
      userInput != null
    }) {
      process(out, userInput)
    }

    out.println()
    out.close()
    in.close()
  }
}
