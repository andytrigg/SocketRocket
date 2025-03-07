package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.FtpHandler
import java.net.Socket


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
class UserCommand : FtpCommand {
    override fun handle(client: Socket, args: List<String>) {
        if (args.isEmpty()) {
            client.getOutputStream().write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters\r\n".toByteArray())
            return
        }
        client.getOutputStream()
            .write("${FtpHandler.USER_NAME_OKAY_NEED_PASSWORD} User ${args[0]} OK, need password\r\n".toByteArray())
    }
}
