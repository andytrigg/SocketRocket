package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.SessionManager
import com.sloshydog.socketrocket.ftp.io.FileSystemProvider
import java.io.IOException
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

class RetrCommand(private val fileSystem: FileSystemProvider) : FtpCommand {

    override fun handle(client: Socket, args: List<String>) {
        val controlOutput = client.getOutputStream()

        if (args.isEmpty()) {
            controlOutput.write("501 Syntax error in parameters.\r\n".toByteArray())
            return
        }
        val filename = args[0]

        if (!fileSystem.fileExists(filename)) {
            controlOutput.write("550 File not found.\r\n".toByteArray())
            return
        }

        val fileSize = fileSystem.getFileSize(filename)
        val dataSocket = establishDataConnection(client)
        if (dataSocket == null) {
            controlOutput.write("425 No data connection established.\r\n".toByteArray())
            return
        }

        controlOutput.write("150 Opening data connection for $filename ($fileSize bytes).\r\n".toByteArray())

        try {
            fileSystem.openFileForRead(filename)?.use { fileInput ->
                dataSocket.getOutputStream().use { dataOutput ->
                    fileInput.copyTo(dataOutput)
                }
            }
            controlOutput.write("226 Transfer complete.\r\n".toByteArray())
        } catch (e: IOException) {
            controlOutput.write("426 Connection closed; transfer aborted.\r\n".toByteArray())
        } finally {
            dataSocket.close()
        }
    }

    private fun establishDataConnection(client: Socket): Socket? {
        // TODO
        return null
//        return SessionManager.getPassiveSocket(client)?.accept()
//            ?: SessionManager.getActiveMode(client)?.let { (address, port) -> Socket(address, port) }
    }
}
