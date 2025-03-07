package com.sloshydog

import kotlinx.cli.*

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
import com.sloshydog.socketrocket.TcpServer

fun main(args: Array<String>) {
    val parser = ArgParser("example")
    val port by parser.option(ArgType.Int, shortName = "p", description = "Port Number", fullName = "port").default(7878)
    parser.parse(args)

    val server = TcpServer(port)

    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down server...")
        server.stop()
    })

    server.start()
}
