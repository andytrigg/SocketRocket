package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.SessionManager
import java.net.InetAddress
import java.net.Socket


/**
 * Copyright (c) 2025. andy@sloshydog.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * The PORT command in FTP is used to switch to Active Mode by instructing t
 * he server to connect to a specific client IP and port for data transfer.
 */
class PortCommand : FtpCommand {
    override fun handle(client: Socket, args: List<String>) {
        val outputStream = client.getOutputStream()
        if (args.isEmpty()) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters.\r\n".toByteArray())
            return
        }
        val parts = args[0].split(",")
        if (parts.size != 6) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters.\r\n".toByteArray())
            return
        }

        val ip = parts.subList(0, 4).joinToString(".")
        val p1 = parts[4].toInt()
        val p2 = parts[5].toInt()

        // Validate 8-bit range for p1 and p2
        if (p1 !in 0..255 || p2 !in 0..255) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Invalid port values.\r\n".toByteArray())
            return
        }
        val port = (parts[4].toInt() * 256) + parts[5].toInt()

        // Validate port range
        if (port !in 1024..65535 || port in INVALID_PORTS) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Port not allowed.\r\n".toByteArray())
            return
        }

        SessionManager.setActiveMode(client, InetAddress.getByName(ip), port)
        outputStream.write("${FtpHandler.COMMAND_OKAY} PORT command successful.\r\n".toByteArray())
    }

    companion object {
        private val INVALID_PORTS = setOf(0, 20, 21, 22, 80, 443)
    }
}
