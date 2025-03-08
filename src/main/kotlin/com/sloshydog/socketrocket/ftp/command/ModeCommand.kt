package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.FtpHandler
import com.sloshydog.socketrocket.ftp.SessionManager
import java.net.Socket

/**
 *  MODE <mode-type>
 *  <mode-type> is a single character that specifies the transfer mode:
 *  S = Stream Mode: Default mode, sends data as a continuous stream.
 *  B = Block Mode: Sends data in blocks with headers.
 *  C = Compressed Mode: Uses run-length encoding to compress data before transfer (rarely used).
 */
class ModeCommand : FtpCommand {
    override fun handle(client: Socket, args: List<String>) {
        val outputStream = client.getOutputStream()

        if (args.isEmpty()) {
            outputStream.write("${FtpHandler.SYNTAX_ERROR} Syntax error in parameters.\r\n".toByteArray())
            return
        }
        val mode = args[0].uppercase()
        when (mode) {
            "S" -> {
                SessionManager.setTransferMode(client, SessionManager.TransferMode.STREAM)
                outputStream.write("${FtpHandler.COMMAND_OKAY} Mode set to Stream.\r\n".toByteArray())
            }
            "B" -> {
                SessionManager.setTransferMode(client, SessionManager.TransferMode.BLOCK)
                outputStream.write("${FtpHandler.COMMAND_OKAY} Mode set to Block.\r\n".toByteArray())
            }
            "C" -> {
                SessionManager.setTransferMode(client, SessionManager.TransferMode.COMPRESSED)
                outputStream.write("${FtpHandler.COMMAND_OKAY} Mode set to Compressed.\r\n".toByteArray())
            }
            else -> {
                outputStream.write("${FtpHandler.COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER} Command not implemented for that parameter.\r\n".toByteArray())
            }
        }
    }
}
