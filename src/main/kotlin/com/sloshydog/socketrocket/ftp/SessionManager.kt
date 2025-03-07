package com.sloshydog.com.sloshydog.socketrocket.ftp

import java.net.Socket
import java.util.concurrent.ConcurrentHashMap


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
object SessionManager {
    private val sessions = ConcurrentHashMap<Socket, Session>()

    data class Session(
        var username: String? = null,
        var isAuthenticated: Boolean = false
    )

    fun setUser(socket: Socket, username: String) {
        sessions[socket] = Session(username, isAuthenticated = false)
    }

    fun authenticate(socket: Socket) {
        sessions[socket]?.isAuthenticated = true
    }

    fun getUser(socket: Socket): String? {
        return sessions[socket]?.username
    }

    fun isAuthenticated(socket: Socket): Boolean {
        return sessions[socket]?.isAuthenticated == true
    }

    fun clearSession(socket: Socket) {
        sessions.remove(socket)
    }
}
