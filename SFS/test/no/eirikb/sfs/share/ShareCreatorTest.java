/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.eirikb.sfs.share;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author eirikb
 */
public class ShareCreatorTest {

    public ShareCreatorTest() {
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
     * Test of createShare method, of class ShareCreator.
     */
    @Test
    public void testCreateShare() {
        System.out.println("createShare");
        String filePath = "/home/eirikb/Desktop";
        Share result = ShareCreator.createShare(filePath);

        long split = result.getShare().getSize() / 2;
        ShareFolder crop1 = ShareCropper.cropShare(result, 0, 123456789);
        ShareFolder crop2 = ShareCropper.cropShare(result, 123456789, 123456789 * 2);
        System.out.println(crop1);
        System.out.println("-------------------------------------------------");
        System.out.println(crop2);


    }
}