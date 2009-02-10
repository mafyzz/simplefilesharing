/*
 * =============================================================================
 * Copyright (c) 2008 Exaid. All rights reserved.
 * =============================================================================
 */
package no.eirikb.sfs.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareFileReader;
import no.eirikb.sfs.share.ShareFileWriter;
import no.eirikb.sfs.share.ShareFolder;
import no.eirikb.sfs.share.ShareUtility;
import no.eirikb.utils.file.MD5File;
import no.eirikb.utils.serializable.ObjectClone;
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
public class SocketTest {

    private final String sharePath = "/home/eirikb/test";
    private final int PARTS = 100;
    private int connected;
    private int done;

    public SocketTest() {
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
    public void simpleTest() throws Exception {
        System.out.println("sockettest");

        final File[] files = {new File(sharePath)};

        System.out.println("Creating hash...");
        String initHash = MD5File.MD5Directory(files[0]);
        System.out.println("Hash: " + initHash);

        Share readShare = ShareUtility.createShare(files, "TestShare");

        Share writeShare = (Share) ObjectClone.clone(readShare);

        System.out.println("Creating shares...");

        final ShareFolder[] readShareFolders = ShareUtility.cropShareToParts(readShare, PARTS);

        final ShareFolder[] writeShareFolders = ShareUtility.cropShareToParts(writeShare, PARTS);

        new Thread() {

            @Override
            public void run() {
                try {
                    System.out.println("Creating server...");
                    ServerSocket serverSocket = new ServerSocket(40000);
                    System.out.println("Accept clients...");
                    connected = 0;
                    for (int j = 0; j < PARTS; j++) {
                        final int i = j;
                        final Socket server = serverSocket.accept();
                        new Thread() {

                            @Override
                            public void run() {
                                try {
                                    ShareFileWriter writer = new ShareFileWriter(
                                            writeShareFolders[i], new File(
                                            "Downloads/" + writeShareFolders[i].getName()));
                                    byte[] b = new byte[server.getReceiveBufferSize()];
                                    int read;
                                    int tot = 0;
                                    while (tot < writeShareFolders[i].getSize()) {
                                        read = server.getInputStream().read(b);
                                        byte[] b2 = new byte[read];
                                        System.arraycopy(b, 0, b2, 0, b2.length);
                                        writer.writeTest(b2, 0);
                                        tot += read;
                                    }
                                    done++;
                                    System.out.println((int) (done * 100.0 / PARTS) + "% Complete");
                                    server.close();
                                } catch (IOException ex) {
                                    Logger.getLogger(SocketTest.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }.start();
                        if (connected + 1 == PARTS) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SocketTest.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        connected++;
                    }
                    serverSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(SocketTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();

        Thread.sleep(1000);


        System.out.println("Creating clients...");

        final Socket[] clients = new Socket[PARTS];

        for (int i = 0; i < PARTS; i++) {
            clients[i] = new Socket("localhost", 40000);
        }

        while (connected < PARTS) {
            Thread.yield();
        }

        System.out.println("Reading and writing shares... ");

        done = 0;
        for (int j = 0; j < PARTS; j++) {
            final int i = j;
            new Thread() {

                public void run() {
                    try {
                        ShareFileReader reader = new ShareFileReader(readShareFolders[i], files[0]);
                        long tot = 0;
                        byte[] b = new byte[clients[i].getSendBufferSize()];
                        while (tot < readShareFolders[i].getSize()) {
                            try {
                                reader.read(b);
                                clients[i].getOutputStream().write(b);
                                tot += b.length;
                            } catch (IOException ex) {
                                Logger.getLogger(SocketTest.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (SocketException ex) {
                        Logger.getLogger(SocketTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        }

        while (done < PARTS) {
            Thread.yield();
        }

        File resultFile = new File("Downloads/" + readShare.getName());
        System.out.println("Creating hash of written share...");
        String resultHash = MD5File.MD5Directory(resultFile);
        System.out.println("Init hash:   " + initHash);
        System.out.println("Result hash: " + resultHash);
        assertEquals(initHash, resultHash);
    }
}