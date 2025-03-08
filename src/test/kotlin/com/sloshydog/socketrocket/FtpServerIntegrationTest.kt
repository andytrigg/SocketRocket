package com.sloshydog.socketrocket

import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.InMemoryIdentityManager
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
class FtpServerIntegrationTest {
    private lateinit var server: TcpServer
    private val serverPort = 12345

    @BeforeAll
    fun startServer() {
        server = TcpServer(FtpHandler(InMemoryIdentityManager), serverPort)
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
    fun `test USER command for unknown user`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("USER hacker")
        output.flush()
        val response = input.readLine()

        assertEquals("331 User hacker OK, need password.", response)

        socket.close()
    }

    @Test
    fun `test USER command for known user`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("USER admin")
        output.flush()
        val response = input.readLine()

        assertEquals("331 User admin OK, need password.", response)

        socket.close()
    }

    @Test
    fun `test PASS command when USER is not set`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("PASS password123")
        output.flush()
        val response = input.readLine()

        assertEquals("503 Login with USER first.", response)

        socket.close()
    }

    @Test
    fun `test valid PASS command when USER is set`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("USER testuser")
        output.flush()
        input.readLine()

        output.println("PASS password123")
        output.flush()
        val response = input.readLine()

        assertEquals("230 testuser Logged in.", response)

        socket.close()
    }

    @Test
    fun `test invalid PASS command when USER is set`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("USER testuser")
        output.flush()
        input.readLine()

        output.println("PASS password1236")
        output.flush()
        val response = input.readLine()

        assertEquals("530 Authentication failed.", response)

        socket.close()
    }

    @Test
    fun `test USER command invalidates previous authenticated user session`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("USER testuser")
        output.flush()
        input.readLine()

        output.println("PASS password123")
        output.flush()
        input.readLine()
        // Authenticated user session now exists

        output.println("USER testuser")
        output.flush()
        val response = input.readLine()

        assertEquals("331 User testuser OK, need password.", response)

        socket.close()
    }

    @Test
    fun `test QUIT command closes current socket connection`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("USER testuser")
        output.flush()
        input.readLine()

        output.println("PASS password123")
        output.flush()
        input.readLine()
        // Authenticated user session now exists

        output.println("QUIT")
        output.flush()
        val response = input.readLine()

        assertEquals("221 Goodbye.", response)
    }

    @Test
    fun `test NOOP command returns 200`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("NOOP")
        output.flush()

        val response = input.readLine()

        assertEquals("200 I successfully did nothing.", response)
    }

    @Test
    fun `test TYPE command changes transfer type`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("TYPE I")
        output.flush()

        val response = input.readLine()

        assertEquals("200 Switching to Binary mode.", response)
    }

    @Test
    fun `test MODE command changes transfer type`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("MODE B")
        output.flush()

        val response = input.readLine()

        assertEquals("200 Mode set to Block.", response)
    }

    @Test
    fun `test STRU command changes file structure`() {
        val socket = Socket("127.0.0.1", serverPort)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))

        output.println("STRU F")
        output.flush()

        val response = input.readLine()

        assertEquals("200 File structure set to F.", response)
    }

    @Test
    fun `test multiple clients can connect and receive responses`() {
        val clients = mutableListOf<Thread>()

        repeat(5) {
            clients.add(Thread {
                val socket = Socket("127.0.0.1", serverPort)
                val output = PrintWriter(socket.getOutputStream(), true)
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))

                output.println("USER $it")
                val response = input.readLine()

                assertEquals("331 User $it OK, need password.", response)

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