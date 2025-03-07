package com.sloshydog.socketrocket.echo

import io.mockk.every
import io.mockk.mockk
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
class EchoTcpHandlerTest {
  private lateinit var handler: EchoTcpHandler
  private lateinit var mockSocket: Socket
  private lateinit var inputStream: ByteArrayInputStream
  private lateinit var outputStream: ByteArrayOutputStream

  @BeforeEach
  fun setup() {
    handler = EchoTcpHandler()
    mockSocket = mockk(relaxed = true)

    // Simulated input and output streams
    inputStream = ByteArrayInputStream("Hello, Server!\n".toByteArray())
    outputStream = ByteArrayOutputStream()

    // Mock socket behavior
    every { mockSocket.getInputStream() } returns inputStream
    every { mockSocket.getOutputStream() } returns outputStream
  }

  @Test
  fun `should return correct handler name`() {
    assertEquals("Echo TCP Handler", handler.name())
  }

  @Test
  fun `should echo received message`() {
    handler.handle(mockSocket)

    val response = outputStream.toString().trim()
    assertEquals("Echo: Hello, Server!", response)
  }
}