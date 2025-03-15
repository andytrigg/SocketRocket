package com.sloshydog.socketrocket.ftp

import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetAddress
import java.net.ServerSocket
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
  fun `getFileStructure should return FILE for unknown socket`() {
    assertEquals(SessionManager.getFileStructure(mockSocket), SessionManager.FileStructure.FILE)
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
  fun `getFileStructure should return FILE for new session`() {
    SessionManager.setUser(mockSocket, "testuser")
    assertEquals(SessionManager.getFileStructure(mockSocket), SessionManager.FileStructure.FILE)
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
  fun `setFileStructure should allow File Structure to be changed for any session`() {
    SessionManager.setFileStructure(mockSocket, SessionManager.FileStructure.RECORD)
    assertEquals(SessionManager.getFileStructure(mockSocket), SessionManager.FileStructure.RECORD)
  }

  @Test
  fun `should correctly set Active Mode`() {
    val address = InetAddress.getByName("192.168.1.5")
    val port = 1025

    SessionManager.setActiveMode(mockSocket, address, port)
    val mode = SessionManager.getDataConnectionMode(mockSocket)

    assertTrue(mode is SessionManager.Session.DataConnection.Active)
    assertEquals(Pair(address, port), (mode as SessionManager.Session.DataConnection.Active).address)
  }

  @Test
  fun `should correctly set Passive Mode`() {
    val serverSocket = mockk<ServerSocket>(relaxed = true) // Mock ServerSocket
    every { serverSocket.localPort } returns 2121
    every { serverSocket.close() } just Runs // Ensure it can be closed without errors

    SessionManager.setPassiveMode(mockSocket, serverSocket)
    val mode = SessionManager.getDataConnectionMode(mockSocket)

    assertTrue(mode is SessionManager.Session.DataConnection.Passive)
    assertEquals(serverSocket, (mode as SessionManager.Session.DataConnection.Passive).socket)
  }

  @Test
  fun `should correctly close socket on session clear when Passive Mode has been set`() {
    val serverSocket = mockk<ServerSocket>(relaxed = true) // Mock ServerSocket
    every { serverSocket.localPort } returns 2121
    every { serverSocket.close() } just Runs // Ensure it can be closed without errors

    SessionManager.setPassiveMode(mockSocket, serverSocket)

    // Verify socket closes on session clear
    SessionManager.clearSession(mockSocket)
    verify { serverSocket.close() }
  }

  @Test
  fun `should clean up resources when switching from Passive to Active Mode`() {
    val serverSocket = mockk<ServerSocket>(relaxed = true)
    every { serverSocket.close() } just Runs

    // Set Passive Mode first
    SessionManager.setPassiveMode(mockSocket, serverSocket)
    assertTrue(SessionManager.getDataConnectionMode(mockSocket) is SessionManager.Session.DataConnection.Passive)

    // Now switch to Active Mode
    val address = InetAddress.getByName("192.168.1.5")
    val port = 1025
    SessionManager.setActiveMode(mockSocket, address, port)

    // Verify Passive Mode socket was closed
    verify { serverSocket.close() }
  }

  @Test
  fun `clearSession should remove session data`() {
    val serverSocket = mockk<ServerSocket>(relaxed = true)
    every { serverSocket.close() } just Runs

    SessionManager.setUser(mockSocket, "testuser")
    SessionManager.authenticate(mockSocket)
    SessionManager.setTransferType(mockSocket, SessionManager.TransferType.BINARY)
    SessionManager.setTransferMode(mockSocket, SessionManager.TransferMode.COMPRESSED)
    SessionManager.setFileStructure(mockSocket, SessionManager.FileStructure.RECORD)
    SessionManager.setPassiveMode(mockSocket, serverSocket)

    SessionManager.clearSession(mockSocket)

    assertNull(SessionManager.getUser(mockSocket))
    assertFalse(SessionManager.isAuthenticated(mockSocket))
    assertEquals(SessionManager.getTransferType(mockSocket), SessionManager.TransferType.ASCII)
    assertEquals(SessionManager.getTransferMode(mockSocket), SessionManager.TransferMode.STREAM)
    assertEquals(SessionManager.getFileStructure(mockSocket), SessionManager.FileStructure.FILE)
    assertTrue(SessionManager.getDataConnectionMode(mockSocket) is SessionManager.Session.DataConnection.Active)

    verify { serverSocket.close() }
  }
}
