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
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.client.Client;
import no.eirikb.sfs.client.LocalShare;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.event.server.DownloadCompleteEvent;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.share.ShareFileWriter;
import no.eirikb.sfs.share.ShareFolder;

/**
 *
 * @author eirikb
 * @author Eirik Brandtzæg <a href="mailto:eirikdb@gmail.com">eirikdb@gmail.com</a>
 */
public class TransferShareEvent extends Event {

    private Integer hash;
    private ShareFolder part;
    private long startBye;

    public TransferShareEvent(Integer hash, ShareFolder part, long startBye) {
        this.hash = hash;
        this.part = part;
        this.startBye = startBye;
    }

    public void execute(SFSServerListener listener, Server client, SFSServer server) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient client) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient client, Server server) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient sfsClient, Client client) {
        try {
            client.setRun(false);
            ShareFileWriter writer = new ShareFileWriter(part,
                    new File(sfsClient.getShareFolder() + part.getName()));
            InputStream in = client.getSocket().getInputStream();
            LocalShare ls = sfsClient.getLocalShares().get(hash);
            byte[] buf = new byte[client.getSocket().getReceiveBufferSize()];
            int b;
            long tot = 0;
            while ((b = in.read(buf)) >= 0) {
                writer.write(buf, b);
                tot += b;
                listener.receiveStatus(ls, part, startBye, tot);
            }
            ls.incShares();
            if (ls.getShares() == ls.getTotalShares()) {
                listener.reveiveDone(ls);
                sfsClient.getClient().sendObject(new DownloadCompleteEvent(hash));
            }
        } catch (IOException ex) {
            Logger.getLogger(TransferShareEvent.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (client != null && client.getSocket() != null) {
                    client.getSocket().close();
                }
            } catch (IOException ex) {
                Logger.getLogger(TransferShareEvent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
