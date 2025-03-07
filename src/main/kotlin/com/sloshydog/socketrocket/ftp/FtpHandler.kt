package com.sloshydog.socketrocket.ftp

import com.sloshydog.socketrocket.TcpHandler
import com.sloshydog.socketrocket.ftp.command.FtpCommandRegistry
import com.sloshydog.socketrocket.ftp.command.PassCommand
import com.sloshydog.socketrocket.ftp.command.UserCommand
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

// RFC 959
class FtpHandler(val identityManager: IdentityManager) : TcpHandler {
    companion object {
        const val USER_LOGGED_IN_PROCEED = 230
        const val USER_NAME_OKAY_NEED_PASSWORD = 331
        const val SYNTAX_ERROR = 501
        const val COMMAND_NOT_IMPLEMENTED = 502
        const val BAD_SEQUENCE_OF_COMMANDS = 503
        const val NOT_LOGGED_IN = 530
    }

    override fun name(): String {
        return "FTP Handler"
    }

    override fun init() {
        FtpCommandRegistry.register("USER", UserCommand())
        FtpCommandRegistry.register("PASS", PassCommand(identityManager))
    }

    override fun handle(clientSocket: Socket) {
        val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

        val line = input.readLine()
        val parts = line.split(" ")
        val command = parts[0]
        val args = parts.drop(1)

        println("Received: $command ${args.joinToString(" ")}")
        val handler = FtpCommandRegistry.getCommand(command)
        if (handler != null) {
            handler.handle(clientSocket, args)
        } else {
            clientSocket.getOutputStream().write("${COMMAND_NOT_IMPLEMENTED} Command not implemented\r\n".toByteArray())
        }
    }
}