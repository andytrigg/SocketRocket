package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.SessionManager
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.net.InetAddress
import java.net.Socket

/**
 * Copyright (c) 2025. andy@sloshydog.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class PortCommandTest {

    private lateinit var portCommand: PortCommand
    private lateinit var mockSocket: Socket
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setup() {
        portCommand = PortCommand()
        mockSocket = mockk(relaxed = true)
        outputStream = ByteArrayOutputStream()

        every { mockSocket.getOutputStream() } returns outputStream

        mockkObject(SessionManager) // Mock SessionManager to track active mode activation
        every { SessionManager.setActiveMode(any(), any(), any()) } just Runs
    }

    @Test
    fun `should successfully parse IP and port and activate active mode`() {
        val portArgs = listOf("192,168,1,5,4,1") // (4 * 256) + 1 = port 1025

        portCommand.handle(mockSocket, portArgs)

        val response = outputStream.toString().trim()
        assertEquals("200 PORT command successful.", response)

        verify { SessionManager.setActiveMode(mockSocket, InetAddress.getByName("192.168.1.5"), 1025) }
    }

    @Test
    fun `should return syntax error for incorrect argument format`() {
        val invalidArgs = listOf("192,168,1") // Not enough parts

        portCommand.handle(mockSocket, invalidArgs)

        val response = outputStream.toString().trim()
        assertEquals("501 Syntax error in parameters.", response)

        verify(exactly = 0) { SessionManager.setActiveMode(any(), any(), any()) }
    }

    @Test
    fun `should return syntax error when argument is missing`() {
        portCommand.handle(mockSocket, emptyList())

        val response = outputStream.toString().trim()
        assertEquals("501 Syntax error in parameters.", response)

        verify(exactly = 0) { SessionManager.setActiveMode(any(), any(), any()) }
    }

    @Test
    fun `should handle invalid port numbers gracefully`() {
        val invalidPortArgs = listOf("192,168,1,5,300,300") // Out of valid byte range

        portCommand.handle(mockSocket, invalidPortArgs)

        val response = outputStream.toString().trim()
        assertEquals("501 Invalid port values.", response)

        verify(exactly = 0) { SessionManager.setActiveMode(any(), any(), any()) }
    }

    //TODO missing test cases.
}