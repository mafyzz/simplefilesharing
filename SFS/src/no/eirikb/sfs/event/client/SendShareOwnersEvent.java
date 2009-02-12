/*
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 */
package no.eirikb.sfs.event.client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.client.Client;
import no.eirikb.sfs.client.ClientAction;
import no.eirikb.sfs.client.LocalShare;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareFolder;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class SendShareOwnersEvent extends Event {

    private Share share;
    private String[] IPs;
    private int[] ports;
    private Client c;

    public SendShareOwnersEvent(Share share, String[] IPs, int[] ports) {
        this.share = share;
        this.IPs = IPs;
        this.ports = ports;
    }

    public void execute(SFSServerListener listener, Server client, SFSServer server) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient client) {
        LocalShare ls = new LocalShare(new File(client.getShareFolder() + share.getShare().getName()), share);
        ls.setTotalShares(IPs.length);

        LocalShare ls2;
        if ((ls2 = client.getLocalShares().put(share.getHash(), ls)) != null) {
            client.getLocalShares().put(ls2.getShare().getHash(), ls);
        }
        ShareFolder[] parts = ShareUtility.cropShareToParts(share, IPs.length);
        listener.shareStartInfo(ls, parts);
        for (int i = 0; i < IPs.length; i++) {
            try {
                Socket c = new Socket(IPs[i], ports[i]);
                TransferShareHackEvent t = new TransferShareHackEvent(share.getHash(), IPs.length, i, c, parts[i]);
                t.execute(listener, client);
            } catch (UnknownHostException ex) {
                Logger.getLogger(SendShareOwnersEvent.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SendShareOwnersEvent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void execute(SFSClientListener listener, SFSClient client, Server server) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient sfsClient, Client client) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
