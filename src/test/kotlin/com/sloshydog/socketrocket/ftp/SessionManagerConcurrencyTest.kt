package com.sloshydog.socketrocket.ftp

import io.mockk.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors


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
class SessionManagerConcurrencyTest {
    private lateinit var mockSocket1: Socket
    private lateinit var mockSocket2: Socket

    @BeforeEach
    fun setup() {
        mockSocket1 = mockk(relaxed = true)
        mockSocket2 = mockk(relaxed = true)

        every { mockSocket1.remoteSocketAddress.toString() } returns "client1"
        every { mockSocket2.remoteSocketAddress.toString() } returns "client2"
    }

    @Test
    fun `should handle multiple clients setting active mode concurrently`() = runBlocking {
        val dispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
        withContext(dispatcher) {
            launch {
                val address = InetAddress.getByName("192.168.1.5")
                val port = 1025
                SessionManager.setActiveMode(mockSocket1, address, port)
            }
            launch {
                val address = InetAddress.getByName("192.168.1.10")
                val port = 2020
                SessionManager.setActiveMode(mockSocket2, address, port)
            }
        }

        val mode1 = SessionManager.getDataConnectionMode(mockSocket1) as? SessionManager.Session.DataConnection.Active
        val mode2 = SessionManager.getDataConnectionMode(mockSocket2) as? SessionManager.Session.DataConnection.Active

        assertNotNull(mode1)
        assertEquals(Pair(InetAddress.getByName("192.168.1.5"), 1025), mode1?.address)

        assertNotNull(mode2)
        assertEquals(Pair(InetAddress.getByName("192.168.1.10"), 2020), mode2?.address)
    }

    @Test
    fun `should handle multiple clients setting passive mode concurrently`() = runBlocking {
        val serverSocket1 = mockk<ServerSocket>(relaxed = true)
        val serverSocket2 = mockk<ServerSocket>(relaxed = true)
        every { serverSocket1.localPort } returns 3000
        every { serverSocket2.localPort } returns 4000

        val dispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
        withContext(dispatcher) {
            launch {
                SessionManager.setPassiveMode(mockSocket1, serverSocket1)
            }
            launch {
                SessionManager.setPassiveMode(mockSocket2, serverSocket2)
            }
        }

        val mode1 = SessionManager.getDataConnectionMode(mockSocket1) as? SessionManager.Session.DataConnection.Passive
        val mode2 = SessionManager.getDataConnectionMode(mockSocket2) as? SessionManager.Session.DataConnection.Passive

        assertNotNull(mode1)
        assertEquals(serverSocket1, mode1?.socket)

        assertNotNull(mode2)
        assertEquals(serverSocket2, mode2?.socket)
    }

    @Test
    fun `should handle concurrent session clearing safely`() {
        val serverSocket1 = mockk<ServerSocket>(relaxed = true)
        val serverSocket2 = mockk<ServerSocket>(relaxed = true)
        every { serverSocket1.close() } just Runs
        every { serverSocket2.close() } just Runs

        SessionManager.setPassiveMode(mockSocket1, serverSocket1)
        SessionManager.setPassiveMode(mockSocket2, serverSocket2)

        val latch = CountDownLatch(2)
        val executor = Executors.newFixedThreadPool(2)

        executor.execute {
            SessionManager.clearSession(mockSocket1)
            latch.countDown()
        }

        executor.execute {
            SessionManager.clearSession(mockSocket2)
            latch.countDown()
        }

        latch.await() // Wait for both threads to finish

        verify { serverSocket1.close() }
        verify { serverSocket2.close() }
    }
}