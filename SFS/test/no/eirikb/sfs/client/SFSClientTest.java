package no.eirikb.sfs.client;

import java.io.File;
import no.eirikb.sfs.event.client.CreateShareEvent;
import no.eirikb.sfs.event.client.GetShareOwnersEvent;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareCreator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author eirikb
 */
public class SFSClientTest {

    public SFSClientTest() {
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

    /**
     * Test of setShares method, of class SFSClient.
     */
    @Test
    public void testSetShares() throws Exception {
        SFSServer server = new SFSServer(new SFSServerListener() {

            public void createShareEvent(Share share) {
                System.out.println("Crate sharrwe!");
            }
        }, 31337);
        int listenPort = (int) (Math.random() * (65536 - 1024) + 1024);



        SFSClient client = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Add Share!");
            }
        }, "localhost", 31337, listenPort);
        String fileName = "/home/eirikb/Desktop/[DB]_Bleach_191_[B10B96E2].avi";
        Share share = ShareCreator.createShare(fileName);
        client.getLocalShares().put(share.getHash(), new LocalShare(new File(fileName), share));
        client.getClient().sendObject(new CreateShareEvent(share));




        SFSClient client2 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("ADD SHARE");
            }
        }, "localhost", 31337, listenPort + 1);

        Thread.sleep(1000);
        for (Share s : client2.getShares()) {
            client2.getClient().sendObject(new GetShareOwnersEvent(s));
        }

    }
}