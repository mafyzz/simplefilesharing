/*
 * =============================================================================
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <eirikb@google.com> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return Eirik Brandtzæg
 * =============================================================================
 */
package no.eirikb.utils.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class MultiCast extends Thread {

    private final static String HOST = "228.5.6.7";
    private final static int IPLENGTH = 21; // 255.255.255.255 65555
    private final static int PORT = 6789;
    private MulticastSocket socket;
    private InetAddress group;
    private String response;

    public MultiCast() {
        init();
    }

    public MultiCast(String response) {
        this.response = response;
        init();
    }

    public void init() {
        try {
            group = InetAddress.getByName(HOST);
            socket = new MulticastSocket(PORT);
            socket.joinGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(MultiCast.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            socket.leaveGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(MultiCast.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String read() {
        try {
            byte[] buf = new byte[IPLENGTH];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            socket.receive(dp);
            return new String(dp.getData());
        } catch (IOException ex) {
            Logger.getLogger(MultiCast.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void send(String msg) {
        try {
            DatagramPacket dp = new DatagramPacket(msg.getBytes(), msg.length(), group, PORT);
            socket.send(dp);
        } catch (IOException ex) {
            Logger.getLogger(MultiCast.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (true) {
            read();
            send(response);
            read();
        }
    }

    public static String getIP() {
        MultiCast m = new MultiCast();
        m.send("Hello");
        m.read();
        return m.read();
    }
}
