/*
 * =============================================================================
 * Copyright (c) 2008 Exaid. All rights reserved.
 * =============================================================================
 */
package no.eirikb.sfs.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author eirikb
 */
public class FileErrorTest {

    private final String FIRSTFILE = "/home/eirikb/test/1/Bleach.avi";
    private final String ERRORFILE = "Downloads/TestShare/1/Bleach.avi";
    private final int BUFFERSIZE = 10000;

    public FileErrorTest() {
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
    public void fileTest() throws Exception {
        RandomAccessFile in1 = new RandomAccessFile(FIRSTFILE, "r");
        RandomAccessFile in2 = new RandomAccessFile(ERRORFILE, "r");
        int pros = 0;
        long size = new File(FIRSTFILE).length();
        System.out.println("Total size: " + size + " bytes");

        long tot = 0;
        int beginning = -1;
        int last = 0;
        while (tot < size) {
            byte[] b1 = new byte[BUFFERSIZE];
            byte[] b2 = new byte[BUFFERSIZE];
            in1.readFully(b1);
            in2.readFully(b2);


            for (int i = 0; i < BUFFERSIZE; i++) {
                if (b1[i] != b2[i]) {
                    if (last < tot + i - 1) {
                        if (beginning < 0) {
                            beginning = last;
                        } else {
                            System.out.println("From: " + beginning + " To: " + last + " Length: " + (last - beginning));
                            beginning = (int) (tot + i);
                        }
                    }
                    last = (int) (tot + i);
                }
            }

            tot += BUFFERSIZE;

            int pros2 = (int) (tot * 100L / size);
            if (pros2 != pros) {
                pros = pros2;
                System.out.println(pros + " % Complete");
            }
        }
    }
}