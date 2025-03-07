package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.com.sloshydog.socketrocket.ftp.SessionManager
import com.sloshydog.socketrocket.ftp.IdentityManager
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
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
    private lateinit var identityManager: IdentityManager
    private lateinit var sessionManager: SessionManager
    private lateinit var command: UserCommand
    private lateinit var mockSocket: Socket
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        identityManager = mockk()
        sessionManager = mockk()
        command = UserCommand(identityManager)
        mockSocket = mockk()
        outputStream = ByteArrayOutputStream()

        every { mockSocket.getOutputStream() } returns outputStream
    }

    @Test
    fun `should return syntax error when no username is provided`() {
        command.handle(mockSocket, emptyList())

        val response = outputStream.toString().trim()
        assertEquals("501 Syntax error in parameters", response)
    }

    @Test
    fun `should accept valid username and set session`() {
        val username = "validUser"
        every { identityManager.isValidUser(username) } returns true
        mockkObject(SessionManager)
        every { SessionManager.setUser(mockSocket, username) } just Runs

        command.handle(mockSocket, listOf(username))

        val response = outputStream.toString().trim()
        assertEquals("331 User $username OK, need password", response)

        verify { SessionManager.setUser(mockSocket, username) }
    }

    @Test
    fun `should reject invalid username`() {
        val username = "invalidUser"
        every { identityManager.isValidUser(username) } returns false

        command.handle(mockSocket, listOf(username))

        val response = outputStream.toString().trim()
        assertEquals("530 Invalid username", response)

        verify(exactly = 0) { sessionManager.setUser(any(), any()) }
    }
}
