/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.eirikb.sfs.share;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        File file = new File("/usr/local/google/home/eirikb/test");
        String initHash = fileToMD5(file);
        Share readShare = ShareUtility.createShare(file);
        long split = readShare.getShare().getSize() / 2;

        ShareFileReader r1 = new ShareFileReader(ShareUtility.cropShare(readShare, 0, split), file);
        ShareFileReader r2 = new ShareFileReader(ShareUtility.cropShare(readShare, split, split * 2), file);

        Share writeShare = ShareUtility.createShare(file);

        File writeFile = new File("downloads/" + readShare.getName());

        ShareFileWriter w1 = new ShareFileWriter(ShareUtility.cropShare(writeShare, 0, split), writeFile);
        ShareFileWriter w2 = new ShareFileWriter(ShareUtility.cropShare(writeShare, split, split * 2), writeFile);


        long end = readShare.getShare().getSize();
        long tot = 0;
        byte[] b = new byte[10000];
        while (tot < end) {
            r1.read(b, 0);
            w1.write(b, b.length);
            tot += b.length;

            r2.read(b, 0);
            w2.write(b, b.length);
            tot += b.length;
        }


        File resultFile = new File("downloads/" + readShare.getName());
        String resultHash = fileToMD5(resultFile);
        System.out.println(initHash);
        System.out.println(resultHash);
        assertEquals(initHash, resultHash);
    }

    private String fileToMD5(File file) {
        if (file.isDirectory()) {
            String hash = "";
            for (File f : file.listFiles()) {
                hash += fileToMD5(f);
            }
            return hash;
        } else {
            InputStream is = null;
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                is = new FileInputStream(file);
                byte[] buffer = new byte[8192];
                int read = 0;
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigInt = new BigInteger(1, md5sum);
                return bigInt.toString(16);
            } catch (IOException ex) {
                Logger.getLogger(ShareFileWriterTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(ShareFileWriterTest.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ShareFileWriterTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return null;
    }
}