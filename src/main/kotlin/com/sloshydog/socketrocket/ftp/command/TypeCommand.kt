package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.SessionManager
import java.net.Socket

/**
 *  TYPE <type-code>
 *  <type-code> can be one of the following:
 *  A = ASCII Mode: Text files (default). Converts line endings (\n <-> \r\n).
 *  I = Image (Binary) Mode: Raw binary transfer. Used for images, executables, and archives.
 *  E = EBCDIC Mode: Legacy text encoding for IBM mainframes. (NOT SUPPORTED)
 *  L = Local Mode: Used for special systems with non-standard byte sizes (rarely used). (NOT SUPPORTED)
 */
class TypeCommand : FtpCommand {
    override fun handle(client: Socket, args: List<String>) {
        val outputStream = client.getOutputStream()

        if (args.isEmpty()) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters.\r\n".toByteArray())
            return
        }
        val type = args[0].uppercase()
        when (type) {
            "A" -> {
                SessionManager.setTransferType(client, SessionManager.TransferType.ASCII)
                outputStream.write("${FtpHandler.COMMAND_OKAY} Switching to ASCII mode.\r\n".toByteArray())
            }

            "I" -> {
                SessionManager.setTransferType(client, SessionManager.TransferType.BINARY)
                outputStream.write("${FtpHandler.COMMAND_OKAY} Switching to Binary mode.\r\n".toByteArray())
            }

            else -> {
                outputStream.write("${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Command not implemented for that parameter.\r\n".toByteArray())
            }
        }
    }
}
