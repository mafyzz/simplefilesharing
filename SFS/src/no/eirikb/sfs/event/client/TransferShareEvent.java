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
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareFileWriter;
import no.eirikb.sfs.share.ShareFolder;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class TransferShareEvent extends Event {

    private Share share;

    public TransferShareEvent(Share share) {
        this.share = share;
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
            ShareFolder readShare = share.getShare();
            ShareFileWriter writer = new ShareFileWriter(share.getShare(),
                    new File(sfsClient.getShareFolder() + readShare.getName()));
            long end = readShare.getSize() - 1;
            InputStream in = client.getSocket().getInputStream();

            byte[] buf = new byte[1024];
            int b;
            while ((b = in.read(buf)) >= 0) {
                writer.write(buf, b);
            }

            System.out.println("Part done!");
            LocalShare ls = sfsClient.getLocalShares().get(share.getHash());
            ls.incShares();
            if (ls.getShares() == ls.getTotalShares()) {
                System.out.println("DONE!!!");
            }
        } catch (IOException ex) {
            Logger.getLogger(TransferShareEvent.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                client.getSocket().close();
            } catch (IOException ex) {
                Logger.getLogger(TransferShareEvent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
