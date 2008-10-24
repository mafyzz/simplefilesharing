/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.event.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.share.Share;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class RequestShareEvent extends Event {

    private Share part;

    public RequestShareEvent(Share part) {
        this.part = part;
    }

    public void execute(SFSServerListener listener, Server client, SFSServer server) {
    }

    public void execute(SFSClientListener listener, SFSClient client) {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    public void execute(SFSClientListener listener, SFSClient client, Server server) {
        FileInputStream in = null;
        try {
            File file = client.getLocalShares().get(part.getHash()).getFile();
            server.sendObject(new TransferShareEvent());
            in = new FileInputStream(file);
            
            byte[] b = new byte[in.available()];
            in.read(b);
            OutputStream out = server.getSocket().getOutputStream();
            out.write(b);
            System.out.println("Sent!!");
        } catch (IOException ex) {
            Logger.getLogger(RequestShareEvent.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(RequestShareEvent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
