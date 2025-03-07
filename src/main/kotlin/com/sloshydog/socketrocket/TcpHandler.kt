package com.sloshydog.socketrocket

import java.net.Socket

interface TcpHandler {
    fun name(): String
    fun init()
    fun handle(clientSocket: Socket)
}
