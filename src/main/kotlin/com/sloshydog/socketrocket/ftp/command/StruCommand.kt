package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.SessionManager
import java.net.Socket

/**
 *  STRU <structure-type>
 *  <structure-type> is a single character that specifies how the file is stored and transferred:
 *  F = File Structure: Default mode, treats files as a continuous sequence of bytes (most common).
 *  R = Record Structure: Transfers files as records with defined lengths (used in mainframes).  (NOT SUPPORTED)
 *  P = Page Structure: Stores files as pages that can be accessed non-sequentially (rarely used).  (NOT SUPPORTED)
 */
class StruCommand : FtpCommand {
    override fun handle(client: Socket, args: List<String>) {
        val outputStream = client.getOutputStream()

        if (args.isEmpty()) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters.\r\n".toByteArray())
            return
        }
        val structure = args[0].uppercase()
        when (structure) {
            "F" -> {
                SessionManager.setFileStructure(client, SessionManager.FileStructure.FILE)
                outputStream.write("${FtpHandler.COMMAND_OKAY} File structure set to F.\r\n".toByteArray())
            }
            "R" -> {
                outputStream.write("${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Record structure not supported.\r\n".toByteArray())
            }
            "P" -> {
                outputStream.write("${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Page structure not supported.\r\n".toByteArray())
            }
            else -> {
                outputStream.write("${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Command not implemented for that parameter.\r\n".toByteArray())
            }
        }
    }
}
