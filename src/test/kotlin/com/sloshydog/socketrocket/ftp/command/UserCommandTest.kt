package com.sloshydog.socketrocket.ftp.command

import io.mockk.every
import io.mockk.mockk
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
class UserCommandTest {
    private lateinit var command: UserCommand
    private lateinit var mockSocket: Socket
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setup() {
        command = UserCommand()
        mockSocket = mockk(relaxed = true)
        outputStream = ByteArrayOutputStream()

        every { mockSocket.getOutputStream() } returns outputStream
    }

    @Test
    fun `should return syntax error when no arguments are provided`() {
        // Act
        command.handle(mockSocket, emptyList())

        // Assert
        val response = outputStream.toString().trim()
        assertEquals("501 Syntax error in parameters", response)
    }

    @Test
    fun `should return user okay need password when username is provided`() {
        // Act
        command.handle(mockSocket, listOf("testuser"))

        // Assert
        val response = outputStream.toString().trim()
        assertEquals("331 User testuser OK, need password", response)
    }
}