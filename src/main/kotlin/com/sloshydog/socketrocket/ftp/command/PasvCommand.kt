package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.SessionManager
import java.net.ServerSocket
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
 * The PASV (Passive Mode) command in FTP is used by a client to request that the server open a port
 * for data connections instead of the client opening one.
 */
class PasvCommand : FtpCommand {
    override fun handle(client: Socket, args: List<String>) {
        val serverSocket = ServerSocket(0) // 0 = OS assigns a free port
        val address = client.localAddress.hostAddress
        val port = serverSocket.localPort

        SessionManager.setPassiveMode(client, serverSocket)

        val p1 = port / 256
        val p2 = port % 256
        client.getOutputStream().write("${FtpHandler.ENTERING_PASSIVE_MODE} Entering Passive Mode ($address,$p1,$p2).\r\n".toByteArray())
    }
}
