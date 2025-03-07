package com.sloshydog.socketrocket.ftp

import com.sloshydog.com.sloshydog.socketrocket.ftp.SessionManager
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
  fun `isAuthenticated should return false for new session`() {
    SessionManager.setUser(mockSocket, "testuser")
    assertFalse(SessionManager.isAuthenticated(mockSocket))
  }

  @Test
  fun `authenticate should mark session as authenticated`() {
    SessionManager.setUser(mockSocket, "testuser")
    SessionManager.authenticate(mockSocket)
    assertTrue(SessionManager.isAuthenticated(mockSocket))
  }

  @Test
  fun `clearSession should remove session data`() {
    SessionManager.setUser(mockSocket, "testuser")
    SessionManager.authenticate(mockSocket)
    SessionManager.clearSession(mockSocket)

    assertNull(SessionManager.getUser(mockSocket))
    assertFalse(SessionManager.isAuthenticated(mockSocket))
  }
}