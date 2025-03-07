package com.sloshydog.socketrocket.echo

import com.sloshydog.com.sloshydog.socketrocket.echo.EchoTcpHandler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.PrintWriter
import java.io.StringReader
import java.io.StringWriter

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

  @BeforeEach
  fun setup() {
    handler = EchoTcpHandler()
  }

  @Test
  fun `should return correct handler name`() {
    assertEquals("Echo TCP Handler", handler.name())
  }

  @Test
  fun `should echo received message`() {
    // Mock input and output streams
    val input = BufferedReader(StringReader("Hello, Server!"))
    val outputStream = StringWriter()
    val output = PrintWriter(outputStream, true)

    handler.handle(input, output)

    // Verify the echoed message
    val result = outputStream.toString().trim()
    assertEquals("Echo: Hello, Server!", result)
  }
}