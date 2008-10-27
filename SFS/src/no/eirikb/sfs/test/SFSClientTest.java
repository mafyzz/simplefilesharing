/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.eirikb.sfs.test;

import java.io.File;
import no.eirikb.sfs.client.LocalShare;
import no.eirikb.sfs.client.SFSClient;
import no.eirikb.sfs.client.SFSClientListener;
import no.eirikb.sfs.event.client.CreateShareEvent;
import no.eirikb.sfs.event.client.GetShareOwnersEvent;
import no.eirikb.sfs.event.client.GetSharesEvent;
import no.eirikb.sfs.sfsserver.SFSServer;
import no.eirikb.sfs.sfsserver.SFSServerListener;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class SFSClientTest {

    public static void main(String[] args) throws Exception {
        SFSServer server = new SFSServer(new SFSServerListener() {

            public void createShareEvent(Share share) {
                System.out.println("Crate sharrwe!");
            }
        }, 31338);
        int listenPort = (int) (Math.random() * (65536 - 1024) + 1024);
        System.out.println("Listen port: " + listenPort);

        SFSClient client = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Add Share!");
            }
        }, "localhost", 31338, listenPort);
        // File file = new File("/users/eirikb/Desktop/Heroes.S03E06.HDTV.XviD-LOL.avi");
        File file = new File("/home/eirikb/test");
        Share share = ShareUtility.createShare(file);
        client.getLocalShares().put(share.getHash(), new LocalShare(file, share));
        client.getClient().sendObject(new CreateShareEvent(share));

        SFSClient client2 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("ADD SHARE");
            }
        }, "localhost", 31338, listenPort + 1);
        
        Thread.sleep(1000);
        
        share = client2.getShares().get(0);
        client2.getClient().sendObject(new GetShareOwnersEvent(share));

        Thread.sleep(10000);
        System.out.println("...");
        SFSClient client3 = new SFSClient(new SFSClientListener() {

            public void addShare(Share share) {
                System.out.println("Add another share!");
            }
        }, "localhost", 31338, listenPort + 2);
        
        client3.getClient().sendObject(new GetSharesEvent());
        
        Thread.sleep(1000);
        
        share = client3.getShares().get(0);
        client3.getClient().sendObject(new GetShareOwnersEvent(share));

    }
}
