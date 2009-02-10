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
import no.eirikb.utils.file.MD5File;
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
    private static String initHash;

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
            private long tot = 0;

            public void addShare(Share share) {
                System.out.println(name + ": Add share");
            }

            public void removeShare(Share share) {
                System.out.println(name + ": Remove share");
            }

            public void receiveStatus(LocalShare ls, ShareFolder share, int partNumber, long bytes) {
                tot += bytes;
                int percent2 = (int) ((tot * 100) / ls.getShare().getShare().getSize());
                if (percent2 != percent) {
                    percent = percent2;
                    System.out.println(percent + "% (part " + partNumber + ")");
                }
            }

            public void sendStatus(LocalShare ls, ShareFolder share, int partNumber, long bytes) {
                //   System.out.println("Send! " + startByte + ' ' + bytes);
            }

            public void receiveDone(LocalShare ls) {
                System.out.println("Receive done.");
                done = true;
            }

            public void sendDone(LocalShare ls) {
                System.out.println("Send done.");
            }

            public void shareStartInfo(ShareFolder[] parts) {
                System.out.println(name + ": Amount of shares: " + parts.length);

            }
        }, listenPort);
    }

    /**
     * *********************************
     * TEST!
     * *********************************
     */
    @Test
    public void initHashTest() {
        System.out.println("Creating hash...");
        final File[] files = {new File(sharePath)};
        initHash = MD5File.MD5Directory(files[0]);
        System.out.println("Hash: " + initHash);
        assertNotNull(initHash);
    }

    @Test
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
        SFSClient c2 = createClient("Client 2", 40002);
        Thread.sleep(1000);

        System.out.println("Client 2: Download share 0 (" + c2.getShares().get(0) + ")");
        done = false;
        c2.getClient().sendObject(new GetShareOwnersEvent(c2.getShares().get(0)));
        while (!done) {
            Thread.yield();
        }

        System.out.println("Client 3: Create");
        SFSClient c3 = createClient("Client 3", 40003);
        Thread.sleep(1000);

        System.out.println("Client 3: Set download folder");
        c3.setShareFolder("Downloads3/");

        System.out.println("Client 3: Download share 0 (" + c3.getShares().get(0) + ")");
        done = false;
        c3.getClient().sendObject(new GetShareOwnersEvent(c3.getShares().get(0)));
        while (!done) {
            Thread.yield();
        }

        System.out.println("Client 4: Create");
        SFSClient c4 = createClient("Client 4", 40004);
        Thread.sleep(1000);

        System.out.println("Client 4: Set download folder");
        c4.setShareFolder("Downloads4/");

        System.out.println("Client 4: Download share 0 (" + c4.getShares().get(0) + ")");
        done = false;
        c4.getClient().sendObject(new GetShareOwnersEvent(c4.getShares().get(0)));
        while (!done) {
            Thread.yield();
        }
    }

    @Test
    public void resultHashTest() {
        System.out.println("Creating hash of written share...");
        File resultFile = new File("Downloads/Test");
        String resultHash = MD5File.MD5Directory(resultFile);
        System.out.println("Init hash:   " + initHash);
        System.out.println("Result hash: " + resultHash);
        assertEquals(initHash, resultHash);
    }
}