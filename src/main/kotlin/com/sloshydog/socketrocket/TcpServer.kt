package com.sloshydog.socketrocket

import com.sloshydog.com.sloshydog.socketrocket.echo.EchoTcpHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
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
class TcpServer(private val port: Int) {
    private val logger = Logger.getLogger(TcpServer::class.java.name)
    private val running = AtomicBoolean(true)
    private lateinit var serverSocket: ServerSocket

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
        val handler = EchoTcpHandler()
        clientSocket.use { socket ->
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val output = PrintWriter(socket.getOutputStream(), true)

            logger.info("👤 New client connected to ${handler.name()}: ${socket.inetAddress}")

            try {
                while (true) {
                    handler.handle(input, output);
                }
            } catch (e: Exception) {
                logger.warning("⚠️ Error with client ${socket.inetAddress}: ${e.message}")
            } finally {
                logger.info("❌ Client ${socket.inetAddress} disconnected")
            }
        }
    }
}

