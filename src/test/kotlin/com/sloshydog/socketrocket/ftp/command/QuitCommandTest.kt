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
class QuitCommandTest {
  private lateinit var command: QuitCommand
  private lateinit var mockSocket: Socket
  private lateinit var outputStream: ByteArrayOutputStream

  @BeforeEach
  fun setup() {
    command = QuitCommand()
    mockSocket = mockk(relaxed = true)
    outputStream = ByteArrayOutputStream()

    every { mockSocket.getOutputStream() } returns outputStream
    every { mockSocket.close() } just Runs
    mockkObject(SessionManager)
    every { SessionManager.clearSession(mockSocket) } just Runs
  }

  @Test
  fun `should send goodbye message, clear session, and close socket`() {
    command.handle(mockSocket, emptyList())

    val response = outputStream.toString().trim()
    assertEquals("221 Goodbye.", response)

    verify { SessionManager.clearSession(mockSocket) }
    verify { mockSocket.close() }
  }
}