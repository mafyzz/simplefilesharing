/*
 * =============================================================================
 * Copyright (c) 2008 Exaid. All rights reserved.
 * =============================================================================
 */
package sfsui.server;

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
 */
public class Server {

    private SFSServer server;

    public static void main(String[] args) {
        try {
            new Server(50000);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Server(int port) throws IOException {
        server = new SFSServer(new SFSServerListener() {

            public void createShareEvent(Share share) {
                System.out.println("SERVER: Create share  - " + share);
            }

            public void onClientConnect(User user) {
                System.out.println("SERVER: Client connect - " + user);
            }

            public void onClientDisconnect(User user) {
                System.out.println("SERVER: Client disconnect - " + user);
            }
        }, port);
    }
}
