package no.eirikb.sfs.test;

import java.io.File;
import no.eirikb.sfs.share.Share;
import no.eirikb.sfs.share.ShareFileReader;
import no.eirikb.sfs.share.ShareFileWriter;
import no.eirikb.sfs.share.ShareUtility;

/**
 *
 * @author eirikb
 * @author <a href="mailto:eirikb@google.com">eirikb@google.com</a>
 */
public class ReadAndWriteTest {

    public static void main(String[] args) {
        //File file = new File("/home/eirikb/Desktop/[DB]_Bleach_191_[B10B96E2].avi");
        //File file = new File("/users/eirikb/Desktop/Heroes.S03E06.HDTV.XviD-LOL.avi");
        File file = new File("/export/home/eirikb/test");
        //File file = new File("/users/eirikb/Bleach.avi");
        Share readShare = ShareUtility.createShare(file);
        long split = readShare.getShare().getSize() / 2;

       //   ShareFileReader r1 = new ShareFileReader(readShare.getShare(), file);

        ShareFileReader r1 = new ShareFileReader(ShareUtility.cropShare(readShare, 0, split), file);
        ShareFileReader r2 = new ShareFileReader(ShareUtility.cropShare(readShare, split, split * 2), file);

        Share writeShare = ShareUtility.createShare(file);

        File writeFile = new File("downloads/" + readShare.getName());

     //      ShareFileWriter w1 = new ShareFileWriter(writeShare.getShare(), writeFile);

        ShareFileWriter w1 = new ShareFileWriter(ShareUtility.cropShare(writeShare, 0, split), writeFile);
        ShareFileWriter w2 = new ShareFileWriter(ShareUtility.cropShare(writeShare, split, split * 2), writeFile);



        //long end = readShare.getShare().getSize();
        //long end = split;
        long end = readShare.getShare().getSize();
        long tot = 0;
        byte[] b = new byte[10000];
        System.out.println(end);
        while (tot < end) {
            //buffer = buffer < tot - end ? buffer : (int)(tot - end);
            r1.read(b, 0);
            w1.write(b, b.length);
            tot += b.length;

            r2.read(b, 0);
            w2.write(b, b.length);
            tot += b.length;
        }
    }
}
