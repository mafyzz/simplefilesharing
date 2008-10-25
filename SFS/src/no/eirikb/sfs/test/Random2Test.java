package no.eirikb.sfs.test;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class Random2Test {

    public static void main(String[] args) {
        new Thread() {

            public void run() {
                try {
                    Random2Test.server();
                } catch (Exception ex) {
                    Logger.getLogger(Random2Test.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
        new Thread() {

            public void run() {
                try {
                    Random2Test.client();
                } catch (Exception ex) {
                    Logger.getLogger(Random2Test.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    public static void client() throws Exception {
        Socket client = new Socket("localhost", 12345);

        RandomAccessFile write = new RandomAccessFile("Bleach2.avi", "rw");
        int b;
        byte[] buf = new byte[1024];
        while ((b = client.getInputStream().read(buf)) >= 0) {
            write.write(buf, 0, b);   
        }
        write.close();
        System.out.println("Client done");
    }

    public static void server() throws Exception {
        ServerSocket serverSocket = new ServerSocket(12345);
        Socket server = serverSocket.accept();

        RandomAccessFile read = new RandomAccessFile("Bleach1.avi", "r");

        int b;
        byte[] buf = new byte[1024];
        while ((b = read.read(buf)) >= 0) {
            server.getOutputStream().write(buf, 0, b);
        }
        server.getOutputStream().flush();
        server.getOutputStream().close();

        System.out.println("Server done");


    }
}
