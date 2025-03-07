package com.sloshydog.socketrocket.ftp

import at.favre.lib.crypto.bcrypt.BCrypt
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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


class InMemoryIdentityManagerTest {

  @BeforeEach
  fun setUp() {
    // Clear and reinitialize users before each test
    InMemoryIdentityManager.addUser("testuser", "password123")
    InMemoryIdentityManager.addUser("admin", "securepass")
  }

  @Test
  fun `isValidUser should return true for existing users`() {
    assertTrue(InMemoryIdentityManager.isValidUser("testuser"))
    assertTrue(InMemoryIdentityManager.isValidUser("admin"))
  }

  @Test
  fun `isValidUser should return false for non-existent users`() {
    assertFalse(InMemoryIdentityManager.isValidUser("nonexistent"))
  }

  @Test
  fun `isValidPassword should return true for correct passwords`() {
    assertTrue(InMemoryIdentityManager.isValidPassword("testuser", "password123"))
    assertTrue(InMemoryIdentityManager.isValidPassword("admin", "securepass"))
  }

  @Test
  fun `isValidPassword should return false for incorrect passwords`() {
    assertFalse(InMemoryIdentityManager.isValidPassword("testuser", "wrongpassword"))
    assertFalse(InMemoryIdentityManager.isValidPassword("admin", "incorrect"))
  }

  @Test
  fun `adding a new user should store hashed password and validate correctly`() {
    InMemoryIdentityManager.addUser("newuser", "newpassword")
    assertTrue(InMemoryIdentityManager.isValidUser("newuser"))
    assertTrue(InMemoryIdentityManager.isValidPassword("newuser", "newpassword"))
  }

  @Test
  fun `hashed password should not match plaintext password`() {
    val hashedPassword = BCrypt.withDefaults().hashToString(12, "plaintext".toCharArray())
    assertNotEquals("plaintext", hashedPassword)
  }
}
