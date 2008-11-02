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
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.sfsserver.User;
import no.eirikb.sfs.share.Share;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class MainServer implements SFSServerListener {

    private SFSServer server;

    public static void main(String[] args) {
        new MainServer();
    }

    public MainServer() {
        try {
            server = new SFSServer(this, 31338);
            try {
                System.out.println("Press enter to terminate...");
                System.in.read();
            } catch (IOException ex) {
                Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            server.close();
        }
    }

    public void createShareEvent(Share share) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onClientConnect(User user) {
        System.out.println("Client connect! " + user.getServer().getIP());
    }

    public void onClientDisconnect(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
