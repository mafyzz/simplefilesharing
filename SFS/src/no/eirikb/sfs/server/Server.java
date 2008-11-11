/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.sfs.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.event.Event;

/**
 * A Blocking server
 * Why not Non-Blocking?
 * As the application is meant for LAN users may not exceed 50...
 * 50 Threads = w/e
 * 
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class Server extends Thread {

    private Socket socket;
    private ServerAction action;
    private ObjectOutputStream objectOut;
    private boolean run;

    public Server(Socket socket, ServerAction action) {
        try {
            this.socket = socket;
            this.action = action;
            objectOut = new ObjectOutputStream(socket.getOutputStream());
            run = true;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        start();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getIP() {
        return socket.getInetAddress().getHostAddress();
    }

    public void sendObject(Object object) {
        try {
            objectOut.writeObject(object);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
            while (run) {
                Event event = (Event) objectIn.readObject();
                action.onServerEvent(this, event);
            }
        }catch (EOFException e) {
            
        } catch (ClassNotFoundException ex) {
            if (run) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            if (run) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            try {
                action.onClientDisconnect(this);
                socket.close();
            } catch (IOException ex) {
                if (run) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
