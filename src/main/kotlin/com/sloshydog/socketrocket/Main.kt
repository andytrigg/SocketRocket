@file:OptIn(ExperimentalCli::class)

package com.sloshydog.socketrocket

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
import com.sloshydog.socketrocket.echo.EchoTcpHandler
import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.InMemoryIdentityManager
import com.sloshydog.socketrocket.ftp.io.LocalFileSystemProvider
import kotlinx.cli.*
import java.io.File

fun main(args: Array<String>) {
    val parser = ArgParser("socketrocket")

    class Echo : Subcommand("echo", "Echo TCP Server") {
        val port by option(ArgType.Int, shortName = "p", description = "Port Number", fullName = "port").default(7878)

        override fun execute() {
            executeServer(EchoTcpHandler(), port)
        }
    }

    class Ftp : Subcommand("ftp", "FTP Server") {
        val port by option(ArgType.Int, shortName = "p", description = "Port Number", fullName = "port").default(21)
        val rootDirPath by option(ArgType.String, shortName = "d", description = "Root directory for FTP server", fullName = "directory")
            .default(".")

        override fun execute() {
            val rootDir = File(rootDirPath)
            if (!rootDir.exists() || !rootDir.isDirectory) {
                println("Error: The specified root directory does not exist or is not a directory: ${rootDir.absolutePath}")
                return
            }
            executeServer(FtpHandler(LocalFileSystemProvider(rootDir), InMemoryIdentityManager), port)
        }
    }

    parser.subcommands(Echo(), Ftp())
    parser.parse(args)
}

private fun executeServer(handler: TcpHandler, port: Int) {
    val server = TcpServer(handler, port)

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down server...")
        server.stop()
    })

    server.start()
}
