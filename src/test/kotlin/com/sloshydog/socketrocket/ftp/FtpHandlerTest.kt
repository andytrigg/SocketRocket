package com.sloshydog.socketrocket.ftp

import com.sloshydog.socketrocket.ftp.command.*
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
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
class FtpHandlerTest {

    private lateinit var handler: FtpHandler
    private lateinit var mockSocket: Socket
    private lateinit var inputStream: ByteArrayInputStream
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var mockCommandHandler: FtpCommand

    @BeforeEach
    fun setup() {
        handler = FtpHandler(mockk(relaxed = true))
        mockSocket = mockk(relaxed = true)
        mockCommandHandler = mockk(relaxed = true)

        // Simulate input and output streams
        outputStream = ByteArrayOutputStream()
    }

    @Test
    fun `should return correct handler name`() {
        assertEquals("FTP Handler", handler.name())
    }

    @Test
    fun `should execute known FTP command`() {
        inputStream = ByteArrayInputStream("USER test_user\r\n".toByteArray())

        every { mockSocket.getInputStream() } returns inputStream
        every { mockSocket.getOutputStream() } returns outputStream

        // Mock command registry behavior
        mockkObject(FtpCommandRegistry)
        every { FtpCommandRegistry.getCommand("USER") } returns mockCommandHandler

        handler.handle(mockSocket)

        // Verify that the command handler was executed
        verify { mockCommandHandler.handle(mockSocket, listOf("test_user")) }

        unmockkObject(FtpCommandRegistry)
    }

    @Test
    fun `should return error for unknown FTP command`() {
        inputStream = ByteArrayInputStream("UNKNOWN test\r\n".toByteArray())

        every { mockSocket.getInputStream() } returns inputStream
        every { mockSocket.getOutputStream() } returns outputStream

        // Mock command registry returning null
        mockkObject(FtpCommandRegistry)
        every { FtpCommandRegistry.getCommand("UNKNOWN") } returns null

        handler.handle(mockSocket)

        unmockkObject(FtpCommandRegistry)

        val response = outputStream.toString().trim()
        assertEquals("502 Command not implemented", response)
    }

    @Test
    fun `should register USER command on init`() {
        mockkObject(FtpCommandRegistry) // Mock the singleton object

        // Ensure the register method is called with expected arguments
        every { FtpCommandRegistry.register("USER", any<UserCommand>()) } just Runs

        handler.init()

        verify(exactly = 1) { FtpCommandRegistry.register("USER", any<UserCommand>()) }

        unmockkObject(FtpCommandRegistry) // Cleanup mock to avoid affecting other tests
    }

    @Test
    fun `should register PASS command on init`() {
        mockkObject(FtpCommandRegistry) // Mock the singleton object

        // Ensure the register method is called with expected arguments
        every { FtpCommandRegistry.register("PASS", any<PassCommand>()) } just Runs

        handler.init()

        verify(exactly = 1) { FtpCommandRegistry.register("PASS", any<PassCommand>()) }

        unmockkObject(FtpCommandRegistry) // Cleanup mock to avoid affecting other tests
    }

    @Test
    fun `should register QUIT command on init`() {
        mockkObject(FtpCommandRegistry) // Mock the singleton object

        // Ensure the register method is called with expected arguments
        every { FtpCommandRegistry.register("QUIT", any<QuitCommand>()) } just Runs

        handler.init()

        verify(exactly = 1) { FtpCommandRegistry.register("QUIT", any<QuitCommand>()) }

        unmockkObject(FtpCommandRegistry) // Cleanup mock to avoid affecting other tests
    }

    @Test
    fun `should register NOOP command on init`() {
        mockkObject(FtpCommandRegistry) // Mock the singleton object

        // Ensure the register method is called with expected arguments
        every { FtpCommandRegistry.register("NOOP", any<NoopCommand>()) } just Runs

        handler.init()

        verify(exactly = 1) { FtpCommandRegistry.register("NOOP", any<NoopCommand>()) }

        unmockkObject(FtpCommandRegistry) // Cleanup mock to avoid affecting other tests
    }

    @Test
    fun `should register TYPE command on init`() {
        mockkObject(FtpCommandRegistry) // Mock the singleton object

        // Ensure the register method is called with expected arguments
        every { FtpCommandRegistry.register("TYPE", any<TypeCommand>()) } just Runs

        handler.init()

        verify(exactly = 1) { FtpCommandRegistry.register("TYPE", any<TypeCommand>()) }

        unmockkObject(FtpCommandRegistry) // Cleanup mock to avoid affecting other tests
    }

    @Test
    fun `should register STRU command on init`() {
        mockkObject(FtpCommandRegistry) // Mock the singleton object

        // Ensure the register method is called with expected arguments
        every { FtpCommandRegistry.register("STRU", any<StruCommand>()) } just Runs

        handler.init()

        verify(exactly = 1) { FtpCommandRegistry.register("STRU", any<StruCommand>()) }

        unmockkObject(FtpCommandRegistry) // Cleanup mock to avoid affecting other tests
    }
}