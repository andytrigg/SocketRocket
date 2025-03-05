package com.sloshydog.socketrocket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket


/**
 * Copyright (c) 2014. gigantiqandy@gmail.com
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
    private var running = true
    private lateinit var serverSocket: ServerSocket

    fun start() {
        serverSocket = ServerSocket(port)
        println("Server started on port $port")

        runBlocking {
            while (running) {
                val clientSocket = serverSocket.accept()
                launch(Dispatchers.IO) { handleClient(clientSocket) }
            }
        }
    }

    fun stop() {
        running = false
        serverSocket.close()
    }

    private fun handleClient(clientSocket: Socket) {
        clientSocket.use { socket ->
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val output = PrintWriter(socket.getOutputStream(), true)

            while (true) {
                val message = input.readLine() ?: break
                println("Received: $message")
                output.println("Echo: $message")
            }
        }
    }
}