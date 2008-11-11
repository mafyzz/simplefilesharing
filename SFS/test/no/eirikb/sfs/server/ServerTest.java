package no.eirikb.sfs.server;

import java.io.File;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.sfsserver.User;
import no.eirikb.sfs.share.Share;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author eirikb
 */
public class ServerTest {

    public ServerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testShares() throws Exception {
        System.out.println("Starting server...");
        new SFSServer(new SFSServerListener() {

            public void createShareEvent(Share share) {
                System.out.println("Server: Crate share");
            }

            public void onClientConnect(User user) {
                System.out.println("Server: Client connect");
            }

            public void onClientDisconnect(User user) {
                System.out.println("Server: Client disconnect");
            }
        }, 31338);

        System.out.println("Client 1: Create client 1");
        int listenPort = (int) (Math.random() * (65536 - 1024) + 1024);
        System.out.println("Client 1: Listen port: " + listenPort);
        SFSClient client1 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Client 1: Add share");
            }

            public void removeShare(Share share) {
                System.out.println("Client 1: Remove share");
            }
        }, "localhost", 31338, listenPort);

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 1: Create a share");
        client1.createShare(new File("/export/home/eirikb/test"));

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 2 : Create client 2");
        SFSClient client2 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Client 2: Add share");
            }

            public void removeShare(Share share) {
                System.out.println("Client 2: Remove share");
            }
        }, "localhost", 31338, listenPort + 1);

        Thread.sleep(1000);
        System.out.println("");
        assertEquals(client1.getShares().size(), 1);
        assertEquals(client1.getShares().size(), client2.getShares().size());

        System.out.println("Client 3: Create clinet 3");
        SFSClient client3 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Client 3: Add share");
            }

            public void removeShare(Share share) {
                System.out.println("Client 3: Remove share");
            }
        }, "localhost", 31338, listenPort + 2);

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 1 : Close connection");
        client1.getClient().setRun(false);
        client1.getClient().getSocket().close();

        Thread.sleep(1000);

        assertEquals(client2.getShares().size(), 0);
        assertEquals(client3.getShares().size(), 0);

        System.out.println("Client 2: Create share");
        client2.createShare(new File("/export/home/eirikb/test"));

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(client2.getShares().size(), 1);
        assertEquals(client3.getShares().size(), 1);

        System.out.println("Client 1: Create client, again");
        client1 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Client 1: Add share");
            }

            public void removeShare(Share share) {
                System.out.println("Client 1: Remove share");
            }
        }, "localhost", 31338, listenPort + 9);

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(client1.getShares().size(), 1);
        assertEquals(client2.getShares().size(), 1);
        assertEquals(client3.getShares().size(), 1);

        client3.createShare(new File("/export/home/eirikb/test"));
        
        Thread.sleep(1000);
        System.out.println("");
        
        assertEquals(client1.getShares().size(), 1);
        assertEquals(client2.getShares().size(), 1);
        assertEquals(client3.getShares().size(), 1);
    }
}