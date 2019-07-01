package scalareplserver

import java.io.{ BufferedReader, InputStream, InputStreamReader, OutputStream, PrintWriter }
import java.net.{ ServerSocket, Socket, SocketException }
import java.util.concurrent.atomic.AtomicBoolean

object ScalaReplServer {
  def main(args: Array[String]): Unit = {
    val serverSocket = new ServerSocket(4444)
    val runnable = new Runnable {
      def run(): Unit = {
        try while (true)
          new Thread(new ReplRunnable(serverSocket.accept()), "Repl").start()
        catch {
          case e: SocketException if serverSocket.isClosed => ()
        }
      }
    }
    new Thread(runnable, "ScalaReplServerListener").start()
    new Repl(System.in, System.out).run()
    serverSocket.close()
  }
}

final class ReplRunnable(socket: Socket) extends Runnable {
  def run(): Unit = {
    new Repl(socket.getInputStream, socket.getOutputStream).run()
    socket.close()
  }
}

final class Repl(in: InputStream, out: OutputStream) {
  def run(): Unit = {
    val in = new BufferedReader(new InputStreamReader(this.in))
    val out = new PrintWriter(this.out, /* autoFlush = */ true)
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
    out.close()
    in.close()
  }
}
