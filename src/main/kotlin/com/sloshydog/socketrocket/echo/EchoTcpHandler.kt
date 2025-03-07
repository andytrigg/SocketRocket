package com.sloshydog.socketrocket.echo

import com.sloshydog.socketrocket.TcpHandler
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.logging.Logger

class EchoTcpHandler : TcpHandler {
    private val logger = Logger.getLogger(EchoTcpHandler::class.java.name)
    override fun name(): String {
        return "Echo TCP Handler"
    }

    override fun init() {
    }

    override fun handle(clientSocket: Socket) {
        val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        val output = PrintWriter(clientSocket.getOutputStream(), true)

        val message = input.readLine()
        logger.info("📩 Received: $message")
        output.println("Echo: $message")
    }
}