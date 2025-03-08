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
class TypeCommandTest {

    private lateinit var typeCommand: TypeCommand
    private lateinit var mockSocket: Socket
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setup() {
        typeCommand = TypeCommand()
        mockSocket = mockk(relaxed = true)
        outputStream = ByteArrayOutputStream()

        every { mockSocket.getOutputStream() } returns outputStream
        mockkObject(SessionManager) // Mock the singleton object
    }

    @Test
    fun `should return syntax error when no arguments are provided`() {
        typeCommand.handle(mockSocket, emptyList())

        val response = outputStream.toString().trim()
        assertEquals("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters.", response)
    }

    @Test
    fun `should switch to ASCII mode`() {
        every { SessionManager.setTransferType(mockSocket, SessionManager.TransferType.ASCII) } just Runs

        typeCommand.handle(mockSocket, listOf("A"))

        val response = outputStream.toString().trim()
        assertEquals("${FtpHandler.COMMAND_OKAY} Switching to ASCII mode.", response)

        verify { SessionManager.setTransferType(mockSocket, SessionManager.TransferType.ASCII) }
    }

    @Test
    fun `should switch to Binary mode`() {
        every { SessionManager.setTransferType(mockSocket, SessionManager.TransferType.BINARY) } just Runs

        typeCommand.handle(mockSocket, listOf("I"))

        val response = outputStream.toString().trim()
        assertEquals("${FtpHandler.COMMAND_OKAY} Switching to Binary mode.", response)

        verify { SessionManager.setTransferType(mockSocket, SessionManager.TransferType.BINARY) }
    }

    @Test
    fun `should return error for unsupported type`() {
        typeCommand.handle(mockSocket, listOf("X"))

        val response = outputStream.toString().trim()
        assertEquals(
            "${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Command not implemented for that parameter.",
            response
        )

        verify(exactly = 0) { SessionManager.setTransferType(any(), any()) }
    }
}