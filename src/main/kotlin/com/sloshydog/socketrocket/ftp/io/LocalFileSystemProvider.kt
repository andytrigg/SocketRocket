package com.sloshydog.socketrocket.ftp.io

import java.io.*


/**
 * Copyright (c) 2025. andy@sloshydog.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class LocalFileSystemProvider(private val rootDir: File) : FileSystemProvider {
    init {
        require(rootDir.isDirectory) { "Provided root directory is not valid: ${rootDir.absolutePath}" }
    }

    override fun listFiles(directory: String): List<String> {
        val dir = resolvePath(directory)
        return dir?.list()?.toList() ?: emptyList()
    }

    override fun fileExists(path: String): Boolean {
        return resolvePath(path)?.exists() == true
    }

    override fun getFileSize(path: String): Long {
        val file = resolvePath(path) ?: return -1 // Ensure non-existent files return -1
        return if (file.exists()) file.length() else -1
    }

    override fun openFileForRead(path: String): InputStream? {
        val file = resolvePath(path) ?: return null
        return if (file.isFile) FileInputStream(file) else null
    }

    override fun openFileForWrite(path: String): OutputStream? {
        val file = resolvePath(path) ?: return null
        return FileOutputStream(file)
    }

    override fun deleteFile(path: String): Boolean {
        return resolvePath(path)?.delete() ?: false
    }

    private fun resolvePath(path: String): File? {
        val resolved = File(rootDir, path).canonicalFile
        return if (resolved.absolutePath.startsWith(rootDir.canonicalPath)) resolved else null
    }
}