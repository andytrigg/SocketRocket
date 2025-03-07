package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.SessionManager
import com.sloshydog.socketrocket.ftp.IdentityManager
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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


class PassCommandTest {

    private lateinit var identityManager: IdentityManager
    private lateinit var passCommand: PassCommand
    private lateinit var clientSocket: Socket
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        identityManager = mockk()
        passCommand = PassCommand(identityManager)
        clientSocket = mockk()
        outputStream = ByteArrayOutputStream()

        every { clientSocket.getOutputStream() } returns outputStream
        mockkObject(SessionManager) // Mock static object
    }

    @Test
    fun `should return syntax error when no password is provided`() {
        passCommand.handle(clientSocket, emptyList())

        assertEquals("501 Syntax error in parameters", outputStream.toString().trim())
    }

    @Test
    fun `should reject authentication if user is already logged in`() {
        every { SessionManager.isAuthenticated(clientSocket) } returns true

        passCommand.handle(clientSocket, listOf("password123"))

        assertEquals("503 User already authenticated.", outputStream.toString().trim())
    }

    @Test
    fun `should prompt login with USER first if username is not set`() {
        every { SessionManager.isAuthenticated(clientSocket) } returns false
        every { SessionManager.getUser(clientSocket) } returns null

        passCommand.handle(clientSocket, listOf("password123"))

        assertEquals("503 Login with USER first.", outputStream.toString().trim())
    }

    @Test
    fun `should authenticate user with valid password`() {
        val username = "testUser"
        every { SessionManager.isAuthenticated(clientSocket) } returns false
        every { SessionManager.getUser(clientSocket) } returns username
        every { identityManager.isValidPassword(username, "password123") } returns true
        every { SessionManager.authenticate(clientSocket) } just Runs

        passCommand.handle(clientSocket, listOf("password123"))

        assertEquals("230 testUser Logged in.", outputStream.toString().trim())
        verify { SessionManager.authenticate(clientSocket) }
    }

    @Test
    fun `should reject invalid password`() {
        val username = "testUser"
        every { SessionManager.isAuthenticated(clientSocket) } returns false
        every { SessionManager.getUser(clientSocket) } returns username
        every { identityManager.isValidPassword(username, "wrongPassword") } returns false

        passCommand.handle(clientSocket, listOf("wrongPassword"))

        assertEquals("530 Authentication failed.", outputStream.toString().trim())

        verify(exactly = 0) { SessionManager.authenticate(any()) }
    }
}