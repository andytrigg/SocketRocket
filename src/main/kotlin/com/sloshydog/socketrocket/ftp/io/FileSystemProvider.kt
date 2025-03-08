package com.sloshydog.socketrocket.ftp.io

import java.io.InputStream
import java.io.OutputStream


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
// TODO Flexibility → Easier to swap out for cloud storage or virtual filesystems later.
interface FileSystemProvider {
    fun listFiles(directory: String): List<String>
    fun fileExists(path: String): Boolean
    fun getFileSize(path: String): Long
    fun openFileForRead(path: String): InputStream?
    fun openFileForWrite(path: String): OutputStream?
    fun deleteFile(path: String): Boolean
}