package no.eirikb.sfs.server;

import java.io.File;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.server.GetShareOwnersEvent;
import no.eirikb.sfs.event.server.RequestShareEvent;
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

    private final String sharePath = "/usr/local/google/home/eirikb/test";

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
        client1.createShare(new File(sharePath), "TestShare");

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
        assertEquals(1, client1.getShares().size());
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
        client1.close();

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(0, client2.getShares().size());
        assertEquals(0, client3.getShares().size());

        System.out.println("Client 2: Create share");
        client2.createShare(new File(sharePath), "TestShare");

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(1, client2.getShares().size());
        assertEquals(1, client3.getShares().size());

        System.out.println("Client 1: Create client, again");
        client1 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Client 1: Add share");
            }

            public void removeShare(Share share) {
                System.out.println("Client 1: Remove share");
            }
        }, "localhost", 31338, listenPort);

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(1, client1.getShares().size());
        assertEquals(1, client2.getShares().size());
        assertEquals(1, client3.getShares().size());

        System.out.println("Client 3: Create share");
        client3.createShare(new File(sharePath), "TestShare");

        Thread.sleep(2000);
        System.out.println("");

        assertEquals(2, client1.getShares().size());
        assertEquals(2, client2.getShares().size());
        assertEquals(2, client3.getShares().size());

        System.out.println("Client 2: Download share 0 (" + client2.getShares().get(0) + ")");
        client2.getClient().sendObject(new GetShareOwnersEvent(client2.getShares().get(0)));

        Thread.sleep(2000);
        System.out.println("");

        System.out.println("Client 2: Disconnect");
        client2.close();

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(2, client1.getShares().size());
        assertEquals(2, client3.getShares().size());

        System.out.println("Client 2 : Create client 2, again");
        client2 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Client 2: Add share");
            }

            public void removeShare(Share share) {
                System.out.println("Client 2: Remove share");
            }
        }, "localhost", 31338, listenPort + 1);

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 2: Download share 0 (" + client2.getShares().get(0) + ")");
        client2.getClient().sendObject(new GetShareOwnersEvent(client2.getShares().get(0)));

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(2, client1.getShares().size());
        assertEquals(2, client2.getShares().size());
        assertEquals(2, client3.getShares().size());
    }
}