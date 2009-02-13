/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.event.server;

import no.eirikb.sfs.client.Client;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.event.client.SendAddShareEvent;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.sfsserver.ShareHolder;
import no.eirikb.sfs.sfsserver.User;
import no.eirikb.sfs.share.Share;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class CreateShareEvent extends Event {

    private Share share;

    public CreateShareEvent(Share share) {
        this.share = share;
    }

    public void execute(SFSServerListener listener, Server client, SFSServer server) {
        server.getShares().add(share);
        ShareHolder shareHolder = new ShareHolder(share);
        server.getShareHolders().put(share.getHash(), shareHolder);
        for (int i = 0; i < server.getUsers().size(); i++) {
            User u = server.getUsers().get(i);
            if (u.getServer().equals(client)) {
                shareHolder.getUsers().add(u);
                break;
            }
        }
        for (int i = 0; i < server.getUsers().size(); i++) {
            server.getUsers().get(i).getServer().sendObject(new SendAddShareEvent(share));
        }
        listener.createShareEvent(share);
    }

    public void execute(SFSClientListener listener, SFSClient client) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient client, Server server) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, SFSClient sfsClient, Client client) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
