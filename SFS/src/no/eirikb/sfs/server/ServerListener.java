/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ServerListener extends Thread {

    private ServerAction action;
    private int port;

    public ServerListener(ServerAction action, int port) {
        this.action = action;
        this.port = port;
        start();
    }

    @Override
    public void run() {
        ServerSocket listener = null;
        try {
            listener = new ServerSocket(port);
            while (true) {
                Server server = new Server(listener.accept(), action);
                action.addServer(server);
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                listener.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
