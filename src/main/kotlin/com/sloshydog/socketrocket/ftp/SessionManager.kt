package com.sloshydog.socketrocket.ftp

import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap


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
object SessionManager {
    enum class TransferType { ASCII, BINARY }
    enum class TransferMode { STREAM, BLOCK, COMPRESSED }
    enum class FileStructure { FILE, RECORD, PAGE }
    enum class DataConnectionMode { ACTIVE, PASSIVE }

    private val sessions = ConcurrentHashMap<Socket, Session>()

    data class Session(
        var username: String? = null,
        var isAuthenticated: Boolean = false,
        var transferType: TransferType = TransferType.ASCII,
        var transferMode: TransferMode = TransferMode.STREAM,
        var fileStructure: FileStructure = FileStructure.FILE
    )

    fun setUser(socket: Socket, username: String) {
        sessions.compute(socket) { _, _ -> Session(username = username) }
    }

    fun authenticate(socket: Socket) {
        sessions.computeIfPresent(socket) { _, session ->
            session.apply { isAuthenticated = true }
        }
    }

    fun getUser(socket: Socket): String? = sessions[socket]?.username

    fun isAuthenticated(socket: Socket): Boolean = sessions.getOrDefault(socket, Session()).isAuthenticated

    fun setTransferType(socket: Socket, type: TransferType) {
        sessions.compute(socket) { _, session ->
            (session ?: Session()).apply { transferType = type }
        }
    }

    fun getTransferType(socket: Socket): TransferType = sessions[socket]?.transferType ?: TransferType.ASCII

    fun setTransferMode(socket: Socket, mode: TransferMode) {
        sessions.compute(socket) { _, session ->
            (session ?: Session()).apply { transferMode = mode }
        }
    }

    fun getTransferMode(socket: Socket): TransferMode = sessions[socket]?.transferMode ?: TransferMode.STREAM

    fun setFileStructure(socket: Socket, structure: FileStructure) {
        sessions.compute(socket) { _, session ->
            (session ?: Session()).apply { fileStructure = structure }
        }
    }

    fun getFileStructure(socket: Socket): FileStructure = sessions[socket]?.fileStructure ?: FileStructure.FILE

    fun clearSession(socket: Socket) {
        sessions.remove(socket)
    }

    fun setPassiveMode(client: Socket, serverSocket: ServerSocket) {
        TODO("Not yet implemented")
    }

    fun setActiveMode(client: Socket, byName: InetAddress?, port: Int) {
        TODO("Not yet implemented")
    }
}
