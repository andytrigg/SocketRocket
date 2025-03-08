package com.sloshydog.socketrocket.ftp.command

import com.sloshydog.socketrocket.ftp.SessionManager
import java.net.Socket

/** TODO
 *  RFC-959 specifies a 530 response to the USER command if the
 *  If file transfer is in progress, the connection must remain
 *  We also stop responding to any further command.
 */
class QuitCommand : FtpCommand {
    override fun handle(client: Socket, args: List<String>) {
        client.getOutputStream().write("221 Goodbye.\r\n".toByteArray())
        SessionManager.clearSession(client)
        client.close()
    }
}
