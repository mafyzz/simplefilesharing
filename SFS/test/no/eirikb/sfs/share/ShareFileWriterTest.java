package no.eirikb.sfs.share;

import java.io.File;
import no.eirikb.utils.serializable.ObjectClone;
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
public class ShareFileWriterTest {

    private final String sharePath = "/home/eirikb/test";
    private final int PARTS = 7;

    public ShareFileWriterTest() {
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
     * Test of write method, of class ShareFileWriter.
     */
    @Test
    public void testWrite() {
        System.out.println("write");
        final File[] files = {new File(sharePath)};

        System.out.println("Creating hash...");
        String initHash = MD5File.MD5Directory(files[0]);
        System.out.println("Hash: " + initHash);
        Share readShare = ShareUtility.createShare(files, "TestShare");

        final long split = readShare.getShare().getSize() / PARTS;

        System.out.println("Creating shares...");

        final ShareFolder[] readers = new ShareFolder[PARTS];

        for (int i = 0; i < PARTS; i++) {
            readers[i] = ShareUtility.cropShare(readShare, i * split, (i + 1) * split);
        }

        System.out.println("Reading and writing shares...");

        for (int i = 0; i < PARTS; i++) {
            final int j = i;
            //       new Thread() {
            //     public void run() {
            ShareFolder part = (ShareFolder) ObjectClone.clone(readers[j]);
            ShareFileReader reader = new ShareFileReader(readers[j], files[0]);
            ShareFileWriter writer = new ShareFileWriter(part,
                    new File("Downloads/" + readers[j].getName()));
            long tot = 0;
            byte[] b = new byte[10000];
            while (tot < split) {
                reader.read(b, 0);
                writer.write(b, b.length);
                tot += b.length;
            }
            System.out.println(tot + " " + split);
            System.out.println((int) ((j + 1) * 100.0 / PARTS) + "% Complete");
        }
        //  }.start();
        // }


        File resultFile = new File("Downloads/" + readShare.getName());
        System.out.println("Creating hash of written share...");
        String resultHash = MD5File.MD5Directory(resultFile);
        System.out.println("Init hash:   " + initHash);
        System.out.println("Result hash: " + resultHash);
        assertEquals(initHash, resultHash);
    }
}