package com.sloshydog.socketrocket.ftp

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
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
class SessionManagerStressTest {
    private val threadPool = Executors.newFixedThreadPool(100).asCoroutineDispatcher()
    private val testClients = mutableListOf<Socket>()

    @BeforeEach
    fun setup() {
        repeat(100) { index ->
            val mockSocket = mockk<Socket>(relaxed = true)
            every { mockSocket.remoteSocketAddress.toString() } returns "mock-client-$index"
            testClients.add(mockSocket)
        }
    }

    @AfterEach
    fun tearDown() {
        testClients.clear()
    }

    @Test
    fun `stress test setting active mode for 100 clients concurrently`() = runBlocking {
        withContext(threadPool) {
            testClients.map { client ->
                async {
                    val address = InetAddress.getByName("192.168.1.5")
                    val port = 1024 + testClients.indexOf(client) // Unique port per client
                    SessionManager.setActiveMode(client, address, port)
                }
            }.awaitAll()
        }

        // Validate all sessions were set correctly
        testClients.forEach { client ->
            val mode = SessionManager.getDataConnectionMode(client)
            assertTrue(mode is SessionManager.Session.DataConnection.Active)
        }
    }

    @Test
    fun `stress test setting passive mode for 100 clients concurrently`() = runBlocking {
        withContext(threadPool) {
            testClients.map { client ->
                async {
                    val serverSocket = mockk<ServerSocket>(relaxed = true)
                    every { serverSocket.localPort } returns 4000 + testClients.indexOf(client) // Unique port per client
                    SessionManager.setPassiveMode(client, serverSocket)
                }
            }.awaitAll()
        }

        // Validate all sessions were set correctly
        testClients.forEach { client ->
            val mode = SessionManager.getDataConnectionMode(client)
            assertTrue(mode is SessionManager.Session.DataConnection.Passive)
        }
    }

    @Test
    fun `stress test clearing sessions for 100 clients concurrently`() = runBlocking {
        withContext(threadPool) {
            testClients.map { client ->
                async {
                    val serverSocket = mockk<ServerSocket>(relaxed = true)
                    every { serverSocket.close() } just Runs
                    SessionManager.setPassiveMode(client, serverSocket)
                }
            }.awaitAll()
        }

        val latch = CountDownLatch(testClients.size)

        withContext(threadPool) {
            testClients.map { client ->
                async {
                    SessionManager.clearSession(client)
                    latch.countDown()
                }
            }.awaitAll()
        }

        latch.await()

        // Validate all sessions are cleared
        testClients.forEach { client ->
            assertTrue(SessionManager.getDataConnectionMode(client) is SessionManager.Session.DataConnection.Active) // Default mode
        }
    }
}