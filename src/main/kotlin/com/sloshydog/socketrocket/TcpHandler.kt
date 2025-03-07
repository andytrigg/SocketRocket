package com.sloshydog.socketrocket

import java.io.BufferedReader
import java.io.PrintWriter

interface TcpHandler {
    fun name(): String
    fun handle(input: BufferedReader, output: PrintWriter)
}
