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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.event.Event;
import no.eirikb.sfs.event.client.TransferShareEvent;
import no.eirikb.sfs.server.Server;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class Client extends Thread {

    private ClientAction action;
    private Socket socket;
    private ObjectOutputStream objectOut;
    private boolean run;

    public Client(ClientAction action) {
        this.action = action;
        run = true;
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        objectOut = new ObjectOutputStream(socket.getOutputStream());
        start();
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendObject(Object object) {
        try {
            objectOut.writeObject(object);
            objectOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        ObjectInputStream objectIn = null;
        try {
            objectIn = new ObjectInputStream(socket.getInputStream());
            while (run) {
                Event event = (Event) objectIn.readObject();
                action.onClientEvent(event);
                if (event instanceof TransferShareEvent) {
                    run = false;
                    socket.close();
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
