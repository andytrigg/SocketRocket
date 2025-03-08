package com.sloshydog.socketrocket.ftp

import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
@TestInstance(TestInstance.Lifecycle.PER_METHOD) // Ensures a fresh instance per test
class SessionManagerTest {
  private lateinit var mockSocket: Socket

  @BeforeEach
  fun setUp() {
    mockSocket = mockk(relaxed = true) // Mock a socket
  }

  @AfterEach
  fun tearDown() {
    SessionManager.clearSession(mockSocket) // Clean up after each test
  }

  @Test
  fun `setUser should store username`() {
    SessionManager.setUser(mockSocket, "testuser")
    assertEquals("testuser", SessionManager.getUser(mockSocket))
  }

  @Test
  fun `getUser should return null for unknown socket`() {
    assertNull(SessionManager.getUser(mockSocket))
  }

  @Test
  fun `getTransferType should return ASCII for unknown socket`() {
    assertEquals(SessionManager.getTransferType(mockSocket), SessionManager.TransferType.ASCII)
  }

  @Test
  fun `getTransferMode should return STREAM for unknown socket`() {
    assertEquals(SessionManager.getTransferMode(mockSocket), SessionManager.TransferMode.STREAM)
  }

  @Test
  fun `isAuthenticated should return false for new session`() {
    SessionManager.setUser(mockSocket, "testuser")
    assertFalse(SessionManager.isAuthenticated(mockSocket))
  }

  @Test
  fun `getTransferType should return ASCII for new session`() {
    SessionManager.setUser(mockSocket, "testuser")
    assertEquals(SessionManager.getTransferType(mockSocket), SessionManager.TransferType.ASCII)
  }

  @Test
  fun `getTransferMode should return STREAM for new session`() {
    SessionManager.setUser(mockSocket, "testuser")
    assertEquals(SessionManager.getTransferMode(mockSocket), SessionManager.TransferMode.STREAM)
  }

  @Test
  fun `authenticate should mark session as authenticated`() {
    SessionManager.setUser(mockSocket, "testuser")
    SessionManager.authenticate(mockSocket)
    assertTrue(SessionManager.isAuthenticated(mockSocket))
  }

  @Test
  fun `setTransferType should allow transfer type to be changed for any session`() {
    SessionManager.setTransferType(mockSocket, SessionManager.TransferType.BINARY)
    assertEquals(SessionManager.getTransferType(mockSocket), SessionManager.TransferType.BINARY)
  }

  @Test
  fun `setTransferMode should allow transfer mode to be changed for any session`() {
    SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.COMPRESSED)
    assertEquals(SessionManager.getTransferMode(mockSocket), SessionManager.TransferMode.COMPRESSED)
  }

  @Test
  fun `clearSession should remove session data`() {
    SessionManager.setUser(mockSocket, "testuser")
    SessionManager.authenticate(mockSocket)
    SessionManager.setTransferType(mockSocket, SessionManager.TransferType.BINARY)
    SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.COMPRESSED)
    SessionManager.clearSession(mockSocket)

    assertNull(SessionManager.getUser(mockSocket))
    assertFalse(SessionManager.isAuthenticated(mockSocket))
    assertEquals(SessionManager.getTransferType(mockSocket), SessionManager.TransferType.ASCII)
    assertEquals(SessionManager.getTransferMode(mockSocket), SessionManager.TransferMode.STREAM)
  }
}