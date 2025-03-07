package com.sloshydog.com.sloshydog.socketrocket.ftp

import com.sloshydog.socketrocket.TcpHandler
import java.io.BufferedReader
import java.io.PrintWriter
import java.util.logging.Logger

class FtpHandler : TcpHandler {
    private val logger = Logger.getLogger(FtpHandler::class.java.name)
    override fun name(): String {
        return "FTP Handler"
    }

    override fun handle(input: BufferedReader, output: PrintWriter) {
        val message = input.readLine()
        logger.info("📩 Received: $message")
        output.println("Echo: $message")
    }
}