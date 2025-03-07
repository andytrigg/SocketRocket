package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.com.sloshydog.socketrocket.ftp.SessionManager
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

/** "Set the username for the current session."
 *  RFC-959 specifies a 530 response to the USER command if the
 *  username is not valid.  If the username is valid is required
 *  ftpd returns a 331 response instead.  In order to prevent a
 *  malicious client from determining valid usernames on a server,
 *  it is suggested by RFC-2577 that a server always return 331 to
 *  the USER command and then reject the combination of username
 *  and password for an invalid username when PASS is provided later.
 */
class UserCommand: FtpCommand {

    override fun handle(client: Socket, args: List<String>) {
        if (args.isEmpty()) {
            client.getOutputStream().write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters\r\n".toByteArray())
            return
        }
        val username = args[0]
        SessionManager.setUser(client, username)
        client.getOutputStream()
            .write("${FtpHandler.USER_NAME_OKAY_NEED_PASSWORD} User $username OK, need password\r\n".toByteArray())
    }
}

//"""Set the username for the current session."""
//# RFC-959 specifies a 530 response to the USER command if the
//# username is not valid.  If the username is valid is required
//# ftpd returns a 331 response instead.  In order to prevent a
//# malicious client from determining valid usernames on a server,
//# it is suggested by RFC-2577 that a server always return 331 to
//# the USER command and then reject the combination of username
//# and password for an invalid username when PASS is provided later.
//if not self.authenticated:
//self.respond('331 Username ok, send password.')
//else:
//# a new USER command could be entered at any point in order
//# to change the access control flushing any user, password,
//# and account information already supplied and beginning the
//# login sequence again.
//self.flush_account()
//msg = 'Previous account information was flushed'
//self.respond(f'331 {msg}, send password.', logfun=logger.info)
//self.username = line