package com.sloshydog.socketrocket.ftp.io

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

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
class LocalFileSystemProviderTest {

  @TempDir
  lateinit var tempRootDir: File

  private lateinit var fileSystem: LocalFileSystemProvider

  @BeforeEach
  fun setup() {
    fileSystem = LocalFileSystemProvider(tempRootDir)
  }

  @Test
  fun `should list files in directory`() {
    File(tempRootDir, "test.txt").apply { createNewFile() }
    File(tempRootDir, "subdir").apply { mkdir() }

    val files = fileSystem.listFiles("")
    assertTrue(files.contains("test.txt"))
    assertTrue(files.contains("subdir"))
  }

  @Test
  fun `should return true for existing file`() {
    File(tempRootDir, "exists.txt").apply { createNewFile() }
    assertTrue(fileSystem.fileExists("exists.txt"))
  }

  @Test
  fun `should return false for non-existent file`() {
    assertFalse(fileSystem.fileExists("missing.txt"))
  }

  @Test
  fun `should return correct file size`() {
    File(tempRootDir, "file.txt").apply {
      writeText("Hello, World!")
    }
    assertEquals(13, fileSystem.getFileSize("file.txt"))
  }

  @Test
  fun `should return -1 for missing file size`() {
    assertEquals(-1, fileSystem.getFileSize("not_found.txt"))
  }

  @Test
  fun `should open file for reading`() {
    File(tempRootDir, "readme.txt").apply {
      writeText("This is a test.")
    }
    fileSystem.openFileForRead("readme.txt")!!.use { input ->
      val content = input.bufferedReader().readText()
      assertEquals("This is a test.", content)
    }
  }

  @Test
  fun `should return null when opening a non-existent file for reading`() {
    assertNull(fileSystem.openFileForRead("not_found.txt"))
  }

  @Test
  fun `should write to a file`() {
    fileSystem.openFileForWrite("output.txt")!!.use { output ->
      output.write("Test Writing".toByteArray())
    }
    assertEquals("Test Writing", File(tempRootDir, "output.txt").readText())
  }

  @Test
  fun `should delete existing file`() {
    val file = File(tempRootDir, "delete_me.txt").apply { createNewFile() }
    assertTrue(fileSystem.deleteFile("delete_me.txt"))
    assertFalse(file.exists())
  }

  @Test
  fun `should return false when deleting non-existent file`() {
    assertFalse(fileSystem.deleteFile("missing.txt"))
  }

  @Test
  fun `should prevent access outside root directory`() {
    val outsideFile = File(tempRootDir.parentFile, "danger.txt")
    outsideFile.createNewFile()

    assertNull(fileSystem.openFileForRead("../danger.txt"))
    assertFalse(fileSystem.fileExists("../danger.txt"))
    assertFalse(fileSystem.deleteFile("../danger.txt"))
  }
}
