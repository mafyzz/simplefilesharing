/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.event.server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.client.Client;
import no.eirikb.sfs.client.ClientAction;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.event.client.RequestShareEvent;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.share.Share;

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
        client.getShares().add(share);
        System.out.println("Start hacking!");
        System.out.println(share);
        for (String s : IPs) {
            System.out.println(s);
        }

        final SFSClientListener l2 = listener;

        try {
            c = new Client(new ClientAction() {

                public void onClientEvent(Event event) {
                    event.execute(l2, c.getSocket());
                }
            });
            c.connect(IPs[0], ports[0]);
            c.sendObject(new RequestShareEvent(share));
            System.out.println("testse");
        } catch (IOException ex) {
            Logger.getLogger(SendShareOwnersEvent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void execute(SFSClientListener listener, SFSClient client, Server server) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(SFSClientListener listener, Socket socket) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
