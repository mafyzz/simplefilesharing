/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.sfsserver;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.server.ServerAction;
import no.eirikb.sfs.server.ServerListener;
import no.eirikb.sfs.share.Share;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class SFSServer implements ServerAction {

    private List<User> users;
    private Map<Integer, ShareHolder> shareHodlers;
    private List<Share> shares;
    private SFSServerListener listener;
    private ServerListener serverListener;

    public SFSServer(SFSServerListener listener, int port) {
        this.listener = listener;
        users = new ArrayList<User>();
        shareHodlers = new Hashtable<Integer, ShareHolder>();
        shares = new ArrayList<Share>();
        serverListener = new ServerListener(this, port);
    }

    public Map<Integer, ShareHolder> getShareHodlers() {
        return shareHodlers;
    }

    public List<Share> getShares() {
        return shares;
    }

    public synchronized List<User> getUsers() {
        return users;
    }

    public synchronized void onServerConnect(Server server) {
        User user = new User(server);
        users.add(user);
        listener.onClientConnect(user);
    }

    public void onServerEvent(Server server, Event event) {
        event.execute(listener, server, this);
    }

    public void onServerDisconnect(Server server) {
        User user = null;
        System.out.println("...");
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getServer().equals(server)) {
                user = users.remove(i);
                break;
            }
        }
        if (user != null) {
            listener.onClientDisconnect(user);
        }
    }

    public void close() {
        serverListener.close();
    }
}
