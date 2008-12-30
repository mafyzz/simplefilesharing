package no.eirikb.sfs.server;

import java.io.File;
import java.io.IOException;
import no.eirikb.sfs.client.LocalShare;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.server.GetShareOwnersEvent;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.sfsserver.User;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareFolder;
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

    private final String sharePath = "/home/eirikb/test";
    private boolean done;

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

    private SFSServer createServer(int port) throws IOException {
        return new SFSServer(new SFSServerListener() {

            public void createShareEvent(Share share) {
                System.out.println("Server: Crate share");
            }

            public void onClientConnect(User user) {
                System.out.println("Server: Client connect");
            }

            public void onClientDisconnect(User user) {
                System.out.println("Server: Client disconnect");
            }
        }, port);
    }

    private SFSClient createClient(final String name, int listenPort) throws IOException {
        return new SFSClient(new SFSClientListener() {

            private int percent;

            public void addShare(Share share) {
                System.out.println(name + ": Add share");
            }

            public void removeShare(Share share) {
                System.out.println(name + ": Remove share");
            }

            public void receiveStatus(LocalShare ls, ShareFolder share, long startByte, long bytes) {
                int percent2 = (int) ((bytes * 100) / ls.getShare().getShare().getSize());
                if (percent2 != percent) {
                    percent = percent2;
                    System.out.println(percent);
                }
            }

            public void sendStatus(LocalShare ls, ShareFolder share, long startByte, long bytes) {
                //   System.out.println("Send! " + startByte + ' ' + bytes);
            }

            public void reveiveDone(LocalShare ls) {
                System.out.println("Done! At last");
                done = true;
            }

            public void sendDone(LocalShare ls) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        }, listenPort);
    }

    //@Test
    public void simpleTest() throws Exception {
        System.out.println("Server: Create");
        createServer(40000);
        System.out.println("Client 1: Create");
        SFSClient c1 = createClient("Client 1", 40001);
        Thread.sleep(1000);
        System.out.println("Client 1: Create share");
        c1.createShare(new File(sharePath), "Test");
        Thread.sleep(1000);
        System.out.println("Client 2: Create");
        SFSClient c2 = createClient("Client ", 40002);
        Thread.sleep(1000);
        System.out.println("Client 2: Download share 0 (" + c2.getShares().get(0) + ")");
        c2.getClient().sendObject(new GetShareOwnersEvent(c2.getShares().get(0)));
        Thread.sleep(30000);
    }

    @Test
    public void testShares() throws Exception {
        System.out.println("Starting server...");
        createServer(40000);

        System.out.println("Client 1: Create client 1");
        int listenPort = (int) (Math.random() * (65536 - 1024) + 1024);
        System.out.println("Client 1: Listen port: " + listenPort);
        SFSClient client1 = createClient("Client 1", listenPort);

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 1: Create a share");
        client1.createShare(new File(sharePath), "TestShare");

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 2 : Create client 2");
        SFSClient client2 = createClient("Client 2", listenPort + 1);

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(1, client1.getShares().size());
        assertEquals(client1.getShares().size(), client2.getShares().size());
        System.out.println("Client 3: Create clinet 3");
        SFSClient client3 = createClient("Client 3", listenPort + 2);

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
        client1 = createClient("Client 1", listenPort);

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
        done = false;
        client2.getClient().sendObject(new GetShareOwnersEvent(client2.getShares().get(0)));

        while (!done) {
            Thread.yield();
        }

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 2: Disconnect");
        client2.close();

        Thread.sleep(1000);
        System.out.println("");

        assertEquals(2, client1.getShares().size());
        assertEquals(2, client3.getShares().size());

        System.out.println("Client 2 : Create client 2, again");
        client2 = createClient("Client 2", listenPort + 1);

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 2: Download share 0 (" + client2.getShares().get(0) + ")");
        done = false;
        client2.getClient().sendObject(new GetShareOwnersEvent(client2.getShares().get(0)));

        while (!done) {
            Thread.yield();
        }

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 2: Create share");
        client2.createShare(new File("/home/eirikb/NetBeansProjects/SFS/Downloads/TestShare"), "AnotherShare");

        Thread.sleep(1000);
        System.out.println("");

        System.out.println("Client 1: Download share 1 (" + client1.getShares().get(1) + ")");
        client1.getClient().sendObject(new GetShareOwnersEvent(client1.getShares().get(1)));

        Thread.sleep(30000);
        System.out.println("");

        assertEquals(2, client1.getShares().size());
        assertEquals(2, client2.getShares().size());
        assertEquals(2, client3.getShares().size());
    }
}