/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.sfsserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.event.client.SendAddSharesEvent;
import no.eirikb.sfs.event.client.SendRemoveShareEvent;
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
    private Map<Integer, ShareHolder> shareHolders;
    private List<Share> shares;
    private SFSServerListener listener;
    private ServerListener serverListener;

    public SFSServer(SFSServerListener listener, int port) throws IOException {
        this.listener = listener;
        users = new ArrayList<User>();
        shareHolders = new Hashtable<Integer, ShareHolder>();
        shares = new ArrayList<Share>();
        serverListener = new ServerListener(this, port);
    }

    public Map<Integer, ShareHolder> getShareHolders() {
        return shareHolders;
    }

    public List<Share> getShares() {
        return shares;
    }

    public synchronized List<User> getUsers() {
        return users;
    }

    public synchronized void onClientConnect(Server server) {
        User user = new User(server);
        users.add(user);
        listener.onClientConnect(user);
        user.getServer().sendObject(new SendAddSharesEvent(shares));
    }

    public void onServerEvent(Server server, Event event) {
        event.execute(listener, server, this);
    }

    public void onClientDisconnect(Server server) {
        User user = null;
        System.out.println("Disconnect! Remove shares?!");
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getServer().equals(server)) {
                user = users.remove(i);
                break;
            }
        }
        if (user != null) {
            System.out.println("User found, removing user...");
            ShareHolder[] shs = shareHolders.values().toArray(new ShareHolder[0]);
            Integer[] is = shareHolders.keySet().toArray(new Integer[0]);
            for (int i = 0; i < shs.length; i++) {
                if (shs[i].getUsers().size() == 1 && shs[i].getUsers().get(0).equals(user)) {
                    shareHolders.remove(is[i]);
                    shares.remove(shs[i].getShare());
                    System.out.println("Remove share! " + shs[i].getShare());
                    for (User u : users) {
                        u.getServer().sendObject(new SendRemoveShareEvent(shs[i].getShare()));
                    }
                }
            }

            listener.onClientDisconnect(user);
        }
    }

    public void close() {
        serverListener.close();
    }
}
