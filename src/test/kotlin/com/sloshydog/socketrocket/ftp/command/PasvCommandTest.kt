package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.SessionManager
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
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
class PasvCommandTest {
    private lateinit var pasvCommand: PasvCommand
    private lateinit var mockSocket: Socket
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setup() {
        pasvCommand = PasvCommand()
        mockSocket = mockk(relaxed = true)
        outputStream = ByteArrayOutputStream()

        every { mockSocket.getOutputStream() } returns outputStream
        every { mockSocket.localAddress.hostAddress } returns "192.168.1.100"

        mockkObject(SessionManager) // Mock SessionManager to track passive mode activation
        every { SessionManager.setPassiveMode(any(), any()) } just Runs
    }

    @Test
    fun `should enter passive mode and return correct response code`() {
        pasvCommand.handle(mockSocket, emptyList())

        val response = outputStream.toString().trim()

        // Validate response format
        assertTrue(response.startsWith("${FtpHandler.ENTERING_PASSIVE_MODE} Entering Passive Mode "))

        verify { SessionManager.setPassiveMode(eq(mockSocket), any()) }
    }

    @Test
    fun `should enter passive mode and return correct response with IP address`() {
        pasvCommand.handle(mockSocket, emptyList())

        val response = outputStream.toString().trim()

        // Extract port numbers
        val regex = """\((\d+)\.(\d+)\.(\d+)\.(\d+),(\d+),(\d+)\)""".toRegex()
        val matchResult = regex.find(response)
        assertTrue(matchResult != null, "Response should contain an IP and port numbers in PASV format.")

        val values = matchResult!!.groupValues.drop(1).map { it.toInt() }

        val h1 = values[0]
        val h2 = values[1]
        val h3 = values[2]
        val h4 = values[3]

        assertTrue(
            h1 == 192 && h2 == 168 && h3 == 1 && h4 == 100,
            "IP address in response should match mock socket address"
        )

        verify { SessionManager.setPassiveMode(eq(mockSocket), any()) }
    }

    @Test
    fun `should enter passive mode and return correct response port in valid range`() {
        pasvCommand.handle(mockSocket, emptyList())

        val response = outputStream.toString().trim()

        // Extract port numbers
        val regex = """\((\d+)\.(\d+)\.(\d+)\.(\d+),(\d+),(\d+)\)""".toRegex()
        val matchResult = regex.find(response)
        val values = matchResult!!.groupValues.drop(1).map { it.toInt() }

        val p1 = values[4]
        val p2 = values[5]

        val expectedPort = (p1 * 256) + p2
        assertTrue(expectedPort in 1024..65535, "Port should be in a valid range")

        verify { SessionManager.setPassiveMode(eq(mockSocket), any()) }
    }
}