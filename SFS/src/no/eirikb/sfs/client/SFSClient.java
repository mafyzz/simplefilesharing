/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.event.client.GetSharesEvent;
import no.eirikb.sfs.event.client.SendUserInfoEvent;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.server.ServerAction;
import no.eirikb.sfs.server.ServerListener;
import no.eirikb.sfs.sfsserver.User;
import no.eirikb.sfs.share.Share;

/**
 *
 * @author eirikbClientCommand
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class SFSClient implements ClientAction, ServerAction {

    private List<Share> shares;
    private Client client;
    private List<User> users;
    private SFSClientListener listener;
    private Map<Integer, LocalShare> localShares;

    public SFSClient(SFSClientListener listener, String host, int port, int listenPort) {
        this.listener = listener;
        client = new Client(this);
        users = new ArrayList<User>();
        shares = new ArrayList<Share>();
        localShares = new Hashtable<Integer, LocalShare>();
        try {
            client.connect(host, port);
            client.sendObject(new SendUserInfoEvent(listenPort));
            client.sendObject(new GetSharesEvent());
            new ServerListener(this, listenPort);
        } catch (IOException ex) {
            Logger.getLogger(SFSClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setShares(List<Share> shares) {
        this.shares = shares;
    }

    public Client getClient() {
        return client;
    }

    public List<Share> getShares() {
        return shares;
    }

    public List<User> getUsers() {
        return users;
    }

    public Map<Integer, LocalShare> getLocalShares() {
        return localShares;
    }

    public void onEvent(Event event) {
        event.execute(listener, this);
    }

    public void addServer(Server server) {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onClientEvent(Event event) {
        event.execute(listener, this);
    }

    public void onServerEvent(Server server, Event event) {
        event.execute(listener, this, server);
    }
}
