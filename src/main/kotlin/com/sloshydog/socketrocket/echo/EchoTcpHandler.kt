package com.sloshydog.com.sloshydog.socketrocket.echo

import com.sloshydog.socketrocket.TcpHandler
import java.io.BufferedReader
import java.io.PrintWriter
import java.util.logging.Logger

class EchoTcpHandler : TcpHandler {
    private val logger = Logger.getLogger(EchoTcpHandler::class.java.name)
    override fun name(): String {
        return "Echo TCP Handler"
    }

    override fun handle(input: BufferedReader, output: PrintWriter) {
        val message = input.readLine()
        logger.info("📩 Received: $message")
        output.println("Echo: $message")
    }
}