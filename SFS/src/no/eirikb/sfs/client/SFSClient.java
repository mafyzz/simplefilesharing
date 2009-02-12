/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.client;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.event.client.TransferShareHackEvent;
import no.eirikb.sfs.event.server.CreateShareEvent;
import no.eirikb.sfs.event.server.GetSharesEvent;
import no.eirikb.sfs.event.server.SendUserInfoEvent;
import no.eirikb.sfs.server.Server;
import no.eirikb.sfs.server.ServerAction;
import no.eirikb.sfs.sfsserver.User;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareUtility;
import no.eirikb.utils.multicast.MultiCast;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class SFSClient implements ClientAction, ServerAction {

    private List<Share> shares;
    private Client client;
    private List<User> users;
    private SFSClientListener listener;
    private Map<Integer, LocalShare> localShares;
    private String shareFolder;

    public SFSClient(SFSClientListener listener2, int listenPort2) throws IOException {
        this.listener = listener2;
        String host = MultiCast.getIP();
        System.out.println(host);
        int port = Integer.parseInt(host.substring(host.indexOf(' ') + 1).trim());
        host = host.substring(0, host.indexOf(' '));
        client = new Client(this);
        users = new ArrayList<User>();
        shares = new ArrayList<Share>();
        localShares = new Hashtable<Integer, LocalShare>();
        shareFolder = "Downloads/";
        client.connect(host, port);
        client.sendObject(new SendUserInfoEvent(listenPort2));
        client.sendObject(new GetSharesEvent());
        final int listenPort = listenPort2;
        final SFSClient sfsClient = this;
        new Thread() {

            public void run() {
                try {
                    ServerSocket serverListener = new ServerSocket(listenPort);
                    while (true) {
                        try {
                            Socket socket = serverListener.accept();
                            TransferShareHackEvent t = new TransferShareHackEvent(socket);
                            t.executeServer(listener, sfsClient);
                        } catch (IOException ex) {
                            Logger.getLogger(SFSClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SFSClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    public void createShare(File file, String name) {
        File[] files = {file};
        createShare(files, name);
    }

    public void createShare(File[] files, String name) {
        Share share = ShareUtility.createShare(files, name);
        localShares.put(share.getHash(), new LocalShare(files[0], share));
        client.sendObject(new CreateShareEvent(share));
    }

    public void close() {
        //serverListener.close();
        try {
            client.getSocket().close();
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

    public synchronized List<Share> getShares() {
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

    public void onClientConnect(Server server) {
        //  throw new UnsupportedOperationException("Not supported yet.");
    }

    public void onClientEvent(Event event) {
        event.execute(listener, this);
    }

    public void onServerEvent(Server server, Event event) {
        event.execute(listener, this, server);
    }

    public SFSClientListener getListener() {
        return listener;
    }

    public void setListener(SFSClientListener listener) {
        this.listener = listener;
    }

    public String getShareFolder() {
        return shareFolder;
    }

    public void setShareFolder(String shareFolder) {
        this.shareFolder = shareFolder;
    }

    public void onClientDisconnect(Server server) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public void closeServerListener() {
        //serverListener.close();
    }

    public synchronized Share getShare(int hash) {
        for (Share share : shares) {
            if (share.getHash() == hash) {
                return share;
            }
        }
        return null;
    }
}
