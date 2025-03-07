package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.SessionManager
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

/** "Set the username for the current session."
 *  RFC-959 specifies a 530 response to the USER command if the
 *  username is not valid.  If the username is valid is required
 *  ftpd returns a 331 response instead.  In order to prevent a
 *  malicious client from determining valid usernames on a server,
 *  it is suggested by RFC-2577 that a server always return 331 to
 *  the USER command and then reject the combination of username
 *  and password for an invalid username when PASS is provided later.
 */
class UserCommand : FtpCommand {

    override fun handle(client: Socket, args: List<String>) {
        val outputStream = client.getOutputStream()
        if (args.isEmpty()) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters\r\n".toByteArray())
            return
        }
        val username = args[0]
        SessionManager.setUser(client, username)
        outputStream
            .write("${FtpHandler.USER_NAME_OKAY_NEED_PASSWORD} User $username OK, need password\r\n".toByteArray())
    }
}
