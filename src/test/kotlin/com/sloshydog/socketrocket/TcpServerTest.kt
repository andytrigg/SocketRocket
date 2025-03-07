package com.sloshydog.socketrocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.jupiter.api.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.test.assertEquals


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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TcpServerTest {
    private lateinit var server: TcpServer
    private val serverPort = 12345

    @BeforeAll
    fun startServer() {
        server = TcpServer(serverPort)
        CoroutineScope(Dispatchers.IO).launch {
            server.start()
        }
        Thread.sleep(500) // Allow server to start
    }

    @AfterAll
    fun stopServer() {
        server.stop()
    }

    @Test
    fun `test client receives echoed message`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("Hello, Server!")
        output.flush()
        val response = input.readLine()

        assertEquals("Echo: Hello, Server!", response)

        socket.close()
    }

    @Test
    fun `test multiple clients can connect and receive responses`() {
        val clients = mutableListOf<Thread>()

        repeat(5) {
            clients.add(Thread {
                val socket = Socket("127.0.0.1", serverPort)
                val output = PrintWriter(socket.getOutputStream(), true)
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))

                output.println("Client-$it")
                val response = input.readLine()

                assertEquals("Echo: Client-$it", response)

                socket.close()
            })
        }

        clients.forEach { it.start() }
        clients.forEach { it.join() }
    }

    @Test
    fun `test invalid port connection fails`() {
        Assertions.assertThrows(Exception::class.java) {
            Socket("127.0.0.1", 9999) // Wrong port
        }
    }
}