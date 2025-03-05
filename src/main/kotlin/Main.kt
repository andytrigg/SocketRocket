package com.sloshydog


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
import java.net.ServerSocket
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter

fun main() {
    val serverPort = 12345
    val serverSocket = ServerSocket(serverPort)  // Open a server socket on the specified port

    println("Server started, waiting for client to connect...")

    while (true) {
        val clientSocket = serverSocket.accept()  // Accept an incoming client connection
        println("Client connected from: ${clientSocket.inetAddress.hostAddress}")

        // Create input and output streams to communicate with the client
        val inputStream = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        val outputStream = PrintWriter(OutputStreamWriter(clientSocket.getOutputStream()), true)

        // Send a welcome message to the client
        outputStream.println("Hello, Client!")

        // Read a message from the client (just for demonstration)
        val clientMessage = inputStream.readLine()
        println("Received from client: $clientMessage")

        // Close the client connection after communication is done
        clientSocket.close()
        println("Client connection closed.\n")
    }
}
