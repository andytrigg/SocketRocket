package com.sloshydog.socketrocket.ftp

import com.sloshydog.com.sloshydog.socketrocket.ftp.FtpCommand
import com.sloshydog.com.sloshydog.socketrocket.ftp.FtpCommandRegistry
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

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
class FtpCommandRegistryTest {
  @BeforeEach
  fun setup() {
    // Reset the command registry before each test (if implemented as an object, use reflection to clear)
    val commandsField = FtpCommandRegistry::class.java.getDeclaredField("commands")
    commandsField.isAccessible = true
    (commandsField.get(null) as MutableMap<*, *>).clear()
  }

  @Test
  fun `should register and retrieve a command`() {
    val mockCommand = mockk<FtpCommand>(relaxed = true)

    FtpCommandRegistry.register("TEST", mockCommand)
    val retrievedCommand = FtpCommandRegistry.getCommand("TEST")

    assertNotNull(retrievedCommand, "Command should be registered and retrievable")
    assertEquals(mockCommand, retrievedCommand, "Retrieved command should match the registered one")
  }

  @Test
  fun `should return null for unregistered command`() {
    val retrievedCommand = FtpCommandRegistry.getCommand("UNKNOWN")
    assertNull(retrievedCommand, "Should return null for an unregistered command")
  }

  @Test
  fun `should be case-insensitive when retrieving commands`() {
    val mockCommand = mockk<FtpCommand>(relaxed = true)

    FtpCommandRegistry.register("test", mockCommand) // Register in lowercase
    val retrievedCommand = FtpCommandRegistry.getCommand("TEST") // Retrieve in uppercase

    assertNotNull(retrievedCommand, "Command retrieval should be case-insensitive")
    assertEquals(mockCommand, retrievedCommand, "Retrieved command should match regardless of case")
  }
}