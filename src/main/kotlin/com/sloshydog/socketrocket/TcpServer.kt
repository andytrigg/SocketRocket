package com.sloshydog.socketrocket

import kotlinx.coroutines.*
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger

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
class TcpServer(private val handler: TcpHandler, val port: Int) {
    private val logger = Logger.getLogger(TcpServer::class.java.name)
    private val running = AtomicBoolean(true)
    private lateinit var serverSocket: ServerSocket

    private val clientCount = AtomicInteger(0)
    // TODO Consider CoroutineScope to manage coroutines cleanly.
//    private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob()) // Coroutine Scope

    fun start() {
        serverSocket = ServerSocket(port)
        logger.info("🚀 Server started on port $port")

        runBlocking {
            try {
                while (running.get()) {
                    val clientSocket = serverSocket.accept()
                    launch(Dispatchers.IO) { handleClient(clientSocket) }
                }
            } catch (e: Exception) {
                if (running.get()) {
                    logger.severe("❌ Server error: ${e.message}")
                }
            }
        }
    }

    fun stop() {
        running.set(false)
        serverSocket.close()
        logger.info("🛑 Server stopped gracefully")
    }

    private fun handleClient(clientSocket: Socket) {
        clientSocket.use { socket ->
            val clientId = clientCount.incrementAndGet()
            logger.info("👤 New client #$clientId connected to ${handler.name()}: ${socket.inetAddress}")
            handler.init()
            try {
                while (true) {
                    handler.handle(socket)
                }
            } catch (e: Exception) {
                logger.warning("⚠️ Error with client ${socket.inetAddress}: ${e.message}")
            } finally {
                clientCount.decrementAndGet()
                logger.info("❌ Client ${socket.inetAddress} disconnected")
            }
        }
    }
}

