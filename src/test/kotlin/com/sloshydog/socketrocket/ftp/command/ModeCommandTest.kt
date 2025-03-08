package com.sloshydog.socketrocket.ftp.command

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
  class ModeCommandTest {
    private lateinit var modeCommand: ModeCommand
    private lateinit var mockSocket: Socket
    private lateinit var outputStream: ByteArrayOutputStream

    @BeforeEach
    fun setup() {
      modeCommand = ModeCommand()
      mockSocket = mockk(relaxed = true)
      outputStream = ByteArrayOutputStream()

      every { mockSocket.getOutputStream() } returns outputStream
      mockkObject(SessionManager) // Mock singleton SessionManager
    }

    @Test
    fun `should return syntax error when no arguments are provided`() {
      modeCommand.handle(mockSocket, emptyList())

      val response = outputStream.toString().trim()
      assertEquals("501 Syntax error in parameters.", response)
    }

    @Test
    fun `should switch to Stream mode`() {
      every { SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.STREAM) } just Runs

      modeCommand.handle(mockSocket, listOf("S"))

      val response = outputStream.toString().trim()
      assertEquals("200 Mode set to Stream.", response)

      verify { SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.STREAM) }
    }

    @Test
    fun `should switch to Block mode`() {
      every { SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.BLOCK) } just Runs

      modeCommand.handle(mockSocket, listOf("B"))

      val response = outputStream.toString().trim()
      assertEquals("200 Mode set to Block.", response)

      verify { SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.BLOCK) }
    }

    @Test
    fun `should switch to Compressed mode`() {
      every { SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.COMPRESSED) } just Runs

      modeCommand.handle(mockSocket, listOf("C"))

      val response = outputStream.toString().trim()
      assertEquals("200 Mode set to Compressed.", response)

      verify { SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.COMPRESSED) }
    }

    @Test
    fun `should return error for unsupported mode`() {
      modeCommand.handle(mockSocket, listOf("X"))

      val response = outputStream.toString().trim()
      assertEquals("504 Command not implemented for that parameter.", response)

      verify(exactly = 0) { SessionManager.setTransferMode(any(), any()) }
    }

  }