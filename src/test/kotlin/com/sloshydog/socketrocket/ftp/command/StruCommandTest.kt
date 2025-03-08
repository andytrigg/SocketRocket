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
class StruCommandTest {
    private lateinit var struCommand: StruCommand
    private lateinit var mockSocket: Socket
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setup() {
        struCommand = StruCommand()
        mockSocket = mockk(relaxed = true)
        outputStream = ByteArrayOutputStream()

        every { mockSocket.getOutputStream() } returns outputStream
        mockkObject(SessionManager) // Mock the singleton SessionManager
    }

    @Test
    fun `should return syntax error when no arguments are provided`() {
        struCommand.handle(mockSocket, emptyList())

        val response = outputStream.toString().trim()
        assertEquals("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters.", response)
    }

    @Test
    fun `should switch to File structure`() {
        every { SessionManager.setFileStructure(mockSocket, SessionManager.FileStructure.FILE) } just Runs

        struCommand.handle(mockSocket, listOf("F"))

        val response = outputStream.toString().trim()
        assertEquals("${FtpHandler.COMMAND_OKAY} File structure set to F.", response)

        verify { SessionManager.setFileStructure(mockSocket, SessionManager.FileStructure.FILE) }
    }

    @Test
    fun `should return error for Record structure`() {
        struCommand.handle(mockSocket, listOf("R"))

        val response = outputStream.toString().trim()
        assertEquals(
            "${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Record structure not supported.",
            response
        )

        verify(exactly = 0) { SessionManager.setFileStructure(any(), any()) }
    }

    @Test
    fun `should return error for Page structure`() {
        struCommand.handle(mockSocket, listOf("P"))

        val response = outputStream.toString().trim()
        assertEquals("${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Page structure not supported.", response)

        verify(exactly = 0) { SessionManager.setFileStructure(any(), any()) }
    }

    @Test
    fun `should return error for unsupported structure`() {
        struCommand.handle(mockSocket, listOf("X"))

        val response = outputStream.toString().trim()
        assertEquals(
            "${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Command not implemented for that parameter.",
            response
        )

        verify(exactly = 0) { SessionManager.setFileStructure(any(), any()) }
    }
}