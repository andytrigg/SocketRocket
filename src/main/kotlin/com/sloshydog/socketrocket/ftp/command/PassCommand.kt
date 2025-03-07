package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.SessionManager
import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.IdentityManager
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
class PassCommand(val identityManager: IdentityManager) : FtpCommand {
    override fun handle(client: Socket, args: List<String>) {
        val outputStream = client.getOutputStream()
        if (args.isEmpty()) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters\r\n".toByteArray())
            return
        }

        if (SessionManager.isAuthenticated(client)) {
            outputStream.write("${FtpHandler.BAD_SEQUENCE_OF_COMMANDS} User already authenticated.\r\n".toByteArray())
            return
        }

        val userName = SessionManager.getUser(client)
        if (userName == null) {
            outputStream.write("${FtpHandler.BAD_SEQUENCE_OF_COMMANDS} Login with USER first.\r\n".toByteArray())
            return
        }

        val password = args[0]
        if (identityManager.isValidPassword(userName, password)) {
            SessionManager.authenticate(client)
            outputStream.write("${FtpHandler.USER_LOGGED_IN_PROCEED} ${userName} Logged in.\r\n".toByteArray())
            return
        } else {
            // TODO Implement mac number of retries
            // TODO support annonynmous users
            outputStream.write("${FtpHandler.NOT_LOGGED_IN} Authentication failed.\r\n".toByteArray())
            return
        }
    }
}