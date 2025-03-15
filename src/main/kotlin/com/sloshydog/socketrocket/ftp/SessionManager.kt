package com.sloshydog.socketrocket.ftp

import com.sloshydog.socketrocket.ftp.SessionManager.Session.DataConnection
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
        var fileStructure: FileStructure = FileStructure.FILE,

        // Default to Active mode but ensure only one connection mode is active at a time
        private var _dataConnectionMode: DataConnection? = DataConnection.Active(null) // Default is active mode
    ) {
        var dataConnectionMode: DataConnection?
            get() = _dataConnectionMode
            set(value) {
                cleanupDataConnection()
                _dataConnectionMode = value
            }

        // Ensure that only one connection type is active at a time
        sealed class DataConnection {
            data class Active(var address: Pair<InetAddress, Int>?) : DataConnection()
            data class Passive(var socket: ServerSocket?) : DataConnection()
        }

        /** Clean up resources when switching modes **/
        fun cleanupDataConnection() {
            when (_dataConnectionMode) {
                is DataConnection.Passive -> {
                    (dataConnectionMode as? DataConnection.Passive)?.socket?.close()
                }

                is DataConnection.Active -> {
                    // No explicit cleanup needed for active mode (it is client-managed)
                }

                null -> {} // No-op
            }
        }
    }

    fun setUser(client: Socket, username: String) {
        sessions.compute(client) { _, _ -> Session(username = username) }
    }

    fun authenticate(client: Socket) {
        sessions.computeIfPresent(client) { _, session ->
            session.apply { isAuthenticated = true }
        }
    }

    fun getUser(client: Socket): String? = sessions[client]?.username

    fun isAuthenticated(client: Socket): Boolean = sessions.getOrDefault(client, Session()).isAuthenticated

    fun setTransferType(client: Socket, type: TransferType) {
        sessions.compute(client) { _, session ->
            (session ?: Session()).apply { transferType = type }
        }
    }

    fun getTransferType(client: Socket): TransferType = sessions[client]?.transferType ?: TransferType.ASCII

    fun setTransferMode(client: Socket, mode: TransferMode) {
        sessions.compute(client) { _, session ->
            (session ?: Session()).apply { transferMode = mode }
        }
    }

    fun getTransferMode(client: Socket): TransferMode = sessions[client]?.transferMode ?: TransferMode.STREAM

    fun setFileStructure(client: Socket, structure: FileStructure) {
        sessions.compute(client) { _, session ->
            (session ?: Session()).apply { fileStructure = structure }
        }
    }

    fun getFileStructure(client: Socket): FileStructure = sessions[client]?.fileStructure ?: FileStructure.FILE

    fun getDataConnectionMode(client: Socket): DataConnection =
        sessions[client]?.dataConnectionMode ?: DataConnection.Active(null)

    fun setPassiveMode(client: Socket, serverSocket: ServerSocket) {
        sessions.compute(client) { _, session ->
            (session ?: Session()).apply {
                dataConnectionMode = Session.DataConnection.Passive(serverSocket)
            }
        }
    }

    fun setActiveMode(client: Socket, address: InetAddress, port: Int) {
        sessions.compute(client) { _, session ->
            (session ?: Session()).apply {
                dataConnectionMode = Session.DataConnection.Active(Pair(address, port))
            }
        }
    }

    @Synchronized
    fun clearSession(client: Socket) {
        val session = sessions.remove(client) ?: return
        session.cleanupDataConnection()
    }
}

